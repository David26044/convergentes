package co.edu.unipiloto.proyectodconvergentes.ui.register

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import co.edu.unipiloto.proyectodconvergentes.R
import co.edu.unipiloto.proyectodconvergentes.ui.RetrofitClient
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
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
            val name = etName.text.toString()
            val identification = etIdentification.text.toString()
            val phoneNumber = etPhoneNumber.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val confirm = etConfirmPassword.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()
                || identification.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            } else if (password != confirm) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            } else {

                lifecycleScope.launch {
                    try {


                        val request = UserRegisterRequest(
                            name,
                            identification,
                            phoneNumber,
                            email,
                            password
                        )
                        val response = RetrofitClient.instance.registerNewUser(request)

                        if (response.isSuccessful){
                            Toast.makeText(this@RegisterActivity, "Usuario con id ${response.body()?.id} creado con exito", Toast.LENGTH_LONG).show()
                        }else{
                            Toast.makeText(this@RegisterActivity, "No se ha registrado al usuario, error del servidor", Toast.LENGTH_LONG).show()
                            val errorBody = response.errorBody()?.string()
                            Log.e("RegisterActivity", "Error del backend: $errorBody")
                            Toast.makeText(this@RegisterActivity, errorBody, Toast.LENGTH_LONG).show()
                        }
                    }catch (e: Exception){
                        Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        Log.e("RegisterActivity", "Error en registro", e)
                    }
                }

            }
        }
    }

}
