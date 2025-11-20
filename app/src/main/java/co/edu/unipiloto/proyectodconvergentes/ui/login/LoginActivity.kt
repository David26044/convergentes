package co.edu.unipiloto.proyectodconvergentes.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import co.edu.unipiloto.proyectodconvergentes.R
import co.edu.unipiloto.proyectodconvergentes.ui.mainMenu.MainMenuActivity
import co.edu.unipiloto.proyectodconvergentes.ui.net.JwtDecoder
import co.edu.unipiloto.proyectodconvergentes.ui.net.RetrofitClient
import co.edu.unipiloto.proyectodconvergentes.ui.net.Roles
import co.edu.unipiloto.proyectodconvergentes.ui.net.TokenManager
import co.edu.unipiloto.proyectodconvergentes.ui.net.hasAnyRole
import co.edu.unipiloto.proyectodconvergentes.ui.register.RegisterActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private val authService by lazy { RetrofitClient.authService }
    private val tokenManager by lazy { TokenManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegisterLink = findViewById<TextView>(R.id.tvRegisterLink)
        // Botón "¿Olvidaste tu contraseña?"|
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)

        tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                toast("Completa todos los campos")
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val resp = authService.loginUser(UserLoginRequest(email, password))
                    when {
                        resp.isSuccessful -> {
                            val token = resp.body()?.token.orEmpty()
                            if (token.isBlank()) {
                                toast("Token vacío")
                                return@launch
                            }

                            // Guardar token
                            tokenManager.saveToken(token)

                            // Decodificar claims del JWT que genera tu backend (authorities -> roles)
                            val claims = JwtDecoder.extractClaims(token)
                            val subject = claims.subject ?: email
                            val roles = claims.roles

                            toast("Bienvenido: $subject")

                            // Navegar según rol (ajusta Activities si lo deseas)
                            val next = when {
                                roles.hasAnyRole(Roles.ADMIN)  -> MainMenuActivity::class.java
                                roles.hasAnyRole(Roles.DRIVER) -> MainMenuActivity::class.java
                                else                           -> MainMenuActivity::class.java
                            }
                            startActivity(Intent(this@LoginActivity, next))
                            finish()
                        }
                        resp.code() == 400 -> toast("Datos inválidos (400)")
                        resp.code() == 401 -> toast("Credenciales inválidas (401)")
                        resp.code() == 403 -> toast("Acceso denegado (403)")
                        resp.code() == 409 -> toast("Conflicto (409)")
                        else -> toast("Error ${resp.code()}")
                    }
                } catch (e: Exception) {
                    toast("Error: ${e.message}")
                }
            }
        }

        tvRegisterLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
