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

class ResetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val etNewPass = findViewById<EditText>(R.id.etNewPassword)
        val etConfirm = findViewById<EditText>(R.id.etConfirmPassword)
        val btnReset = findViewById<Button>(R.id.btnConfirmReset)

        // Captura del token del enlace (myapp://reset?token=XYZ)
        val token = intent?.data?.getQueryParameter("token")

        btnReset.setOnClickListener {
            val pass = etNewPass.text.toString()
            val confirm = etConfirm.text.toString()

            if (pass.isEmpty() || confirm.isEmpty()) {
                toast("Completa todos los campos")
                return@setOnClickListener
            }
            if (pass != confirm) {
                toast("Las contraseñas no coinciden")
                return@setOnClickListener
            }

            if (token.isNullOrBlank()) {
                toast("Token inválido o ausente")
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val authorizedService = RetrofitClient.createAuthorizedService(token)
                    val resp = authorizedService.resetPassword(pass)
                    if (resp.isSuccessful) {
                        toast("Contraseña actualizada")
                        finish()
                    } else {
                        toast("Error (${resp.code()})")
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
