package co.edu.unipiloto.proyectodconvergentes.ui.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import co.edu.unipiloto.proyectodconvergentes.R
import co.edu.unipiloto.proyectodconvergentes.ui.incident.RegisterIncidentActivity
import co.edu.unipiloto.proyectodconvergentes.ui.net.JwtDecoder
import co.edu.unipiloto.proyectodconvergentes.ui.net.RetrofitClient
import co.edu.unipiloto.proyectodconvergentes.ui.net.Roles
import co.edu.unipiloto.proyectodconvergentes.ui.net.TokenManager
import co.edu.unipiloto.proyectodconvergentes.ui.net.hasAnyRole
import co.edu.unipiloto.proyectodconvergentes.ui.order.RegisterOrderActivity
import kotlinx.coroutines.launch
import com.google.gson.Gson
import retrofit2.Response
import co.edu.unipiloto.proyectodconvergentes.ui.net.ApiError
import co.edu.unipiloto.proyectodconvergentes.ui.net.FieldError

class RegisterActivity : AppCompatActivity() {

    private val authService by lazy { RetrofitClient.authService }
    private val tokenManager by lazy { TokenManager(this) }
    private val gson by lazy { Gson() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etName = findViewById<EditText>(R.id.etName)
        val etIdentification = findViewById<EditText>(R.id.etIdentification)
        val etPhoneNumber = findViewById<EditText>(R.id.etPhoneNumber)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            // limpia errores previos
            listOf(etName, etIdentification, etPhoneNumber, etEmail, etPassword, etConfirmPassword)
                .forEach { it.error = null }

            val name = etName.text.toString().trim()
            val identification = etIdentification.text.toString().trim()
            val phoneNumber = etPhoneNumber.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()
            val confirm = etConfirmPassword.text.toString()

            if (name.isEmpty() || identification.isEmpty() || phoneNumber.isEmpty() ||
                email.isEmpty() || password.isEmpty() || confirm.isEmpty()
            ) {
                toast("Completa todos los campos")
                return@setOnClickListener
            }
            if (password != confirm) {
                etConfirmPassword.error = "Las contraseñas no coinciden"
                toast("Las contraseñas no coinciden")
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val req = UserRegisterRequest(
                        email = email,
                        password = password,
                        name = name,
                        phoneNumber = phoneNumber,
                        identification = identification
                    )

                    val resp = authService.registerNewUser(req)

                    when {
                        resp.isSuccessful -> {
                            val body = resp.body()
                            val token = body?.token.orEmpty()

                            tokenManager.saveToken(token)

                            val claims = JwtDecoder.extractClaims(token)
                            val subject = claims.subject ?: body?.name ?: email
                            val roles = claims.roles

                            toast("¡Cuenta creada! Bienvenido: $subject")

                            val next = when {
                                roles.hasAnyRole(Roles.ADMIN)  -> RegisterIncidentActivity::class.java
                                roles.hasAnyRole(Roles.DRIVER) -> RegisterOrderActivity::class.java
                                else                           -> RegisterOrderActivity::class.java
                            }
                            startActivity(Intent(this@RegisterActivity, next))
                            finish()
                        }

                        // 400: errores de validación (@Valid -> MethodArgumentNotValidException)
                        resp.code() == 400 -> {
                            val api = resp.parseApiError(gson)
                            api?.fieldErrors?.let { list ->
                                applyFieldErrors(
                                    list = list,
                                    etName = etName,
                                    etIdentification = etIdentification,
                                    etPhone = etPhoneNumber,
                                    etEmail = etEmail,
                                    etPassword = etPassword
                                )
                            }
                            toast(api?.message ?: "Datos inválidos (400)")
                        }

                        // 409: recurso existente
                        resp.code() == 409 -> {
                            val api = resp.parseApiError(gson)
                            api?.fieldErrors?.let { list ->
                                applyFieldErrors(
                                    list = list,
                                    etName = etName,
                                    etIdentification = etIdentification,
                                    etPhone = etPhoneNumber,
                                    etEmail = etEmail,
                                    etPassword = etPassword
                                )
                            }
                            // Mensaje general
                            toast(api?.message ?: "Usuario ya registrado (409)")
                        }

                        resp.code() == 401 -> toast("No autorizado (401)")
                        resp.code() == 403 -> toast("Acceso denegado (403)")

                        else -> {
                            val raw = resp.errorBody()?.string()
                            Log.e("RegisterActivity", "Error ${resp.code()}: $raw")
                            val api = resp.parseApiError(gson)
                            api?.fieldErrors?.let { list ->
                                applyFieldErrors(
                                    list = list,
                                    etName = etName,
                                    etIdentification = etIdentification,
                                    etPhone = etPhoneNumber,
                                    etEmail = etEmail,
                                    etPassword = etPassword
                                )
                            }
                            toast(api?.message ?: "Error ${resp.code()}")
                        }
                    }
                } catch (e: Exception) {
                    toast("Error: ${e.message}")
                    Log.e("RegisterActivity", "Excepción en registro", e)
                }
            }
        }
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    // --- Helpers de parseo y pintado ---

    private fun <T> Response<T>.parseApiError(gson: Gson): ApiError? {
        return try {
            val raw = errorBody()?.string() ?: return null
            gson.fromJson(raw, ApiError::class.java)
        } catch (e: Exception) {
            Log.e("RegisterActivity", "No se pudo parsear ApiError", e)
            null
        }
    }

    private fun applyFieldErrors(
        list: List<FieldError>,
        etName: EditText,
        etIdentification: EditText,
        etPhone: EditText,
        etEmail: EditText,
        etPassword: EditText
    ) {
        list.forEach { fe ->
            when (fe.field?.lowercase()) {
                "name" -> etName.error = fe.message ?: "Inválido"
                "identification" -> etIdentification.error = fe.message ?: "Inválido"
                "phonenumber" -> etPhone.error = fe.message ?: "Inválido"
                "email" -> etEmail.error = fe.message ?: "Inválido"
                "password" -> etPassword.error = fe.message ?: "Inválido"
                else -> { /* desconocido: lo dejamos solo en toast */ }
            }
        }
    }
}
