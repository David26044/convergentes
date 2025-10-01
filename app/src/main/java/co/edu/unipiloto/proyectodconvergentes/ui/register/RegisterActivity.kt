package co.edu.unipiloto.proyectodconvergentes.ui.register


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import co.edu.unipiloto.proyectodconvergentes.R
import co.edu.unipiloto.proyectodconvergentes.ui.mainMenu.MainMenuActivity
import co.edu.unipiloto.proyectodconvergentes.ui.net.RetrofitClient
import co.edu.unipiloto.proyectodconvergentes.ui.net.TokenManager
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private val authService by lazy { RetrofitClient.authService }
    private val tokenManager by lazy { TokenManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etName           = findViewById<EditText>(R.id.etName)
        val etEmail          = findViewById<EditText>(R.id.etEmail)
        val etPhone          = findViewById<EditText>(R.id.etPhoneNumber)
        val etIdentification = findViewById<EditText>(R.id.etIdentification)
        val etPassword       = findViewById<EditText>(R.id.etPassword)
        val btnRegister      = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val name  = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val idn   = etIdentification.text.toString().trim()
            val pass  = etPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || idn.isEmpty() || pass.isEmpty()) {
                toast("Completa todos los campos")
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val req = UserRegisterRequest(
                        email = email,
                        password = pass,
                        name = name,
                        phoneNumber = phone,
                        identification = idn
                    )

                    val resp = authService.registerNewUser(req)

                    if (!resp.isSuccessful) {
                        when (resp.code()) {
                            400 -> toast("Datos inválidos (400)")
                            401 -> toast("No autorizado (401)")
                            403 -> toast("Acceso denegado (403)")
                            409 -> toast("Correo ya registrado (409)")
                            else -> toast("Error ${resp.code()}")
                        }
                        return@launch
                    }

                    val body = resp.body()
                    if (body == null || body.token.isBlank()) {
                        toast("No se recibió token")
                        return@launch
                    }

                    // Guardar token y navegar
                    tokenManager.saveToken(body.token)
                    startActivity(Intent(this@RegisterActivity, MainMenuActivity::class.java))
                    finish()

                } catch (e: Exception) {
                    toast("Error: ${e.message}")
                }
            }
        }
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}