package co.edu.unipiloto.proyectodconvergentes.ui.login

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import co.edu.unipiloto.proyectodconvergentes.R
import co.edu.unipiloto.proyectodconvergentes.ui.net.RetrofitClient
import kotlinx.coroutines.launch

class ForgotPasswordActivity : AppCompatActivity() {

    private val api by lazy { RetrofitClient.authService }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val etEmail = findViewById<EditText>(R.id.etEmailForgot)
        val btnSend = findViewById<Button>(R.id.btnSendReset)

        btnSend.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (email.isEmpty()) {
                toast("Ingresa tu correo")
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val resp = api.forgotPassword(email)
                    if (resp.isSuccessful) {
                        toast("Si el correo existe, se enviaron instrucciones")
                        finish()
                    } else {
                        toast("Error al solicitar enlace (${resp.code()})")
                    }
                } catch (e: Exception) {
                    toast("Error: ${e.message}")
                }
            }
        }
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
