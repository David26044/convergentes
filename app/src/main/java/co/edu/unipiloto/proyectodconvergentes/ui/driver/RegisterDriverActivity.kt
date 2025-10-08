package co.edu.unipiloto.proyectodconvergentes.ui.driver

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import co.edu.unipiloto.proyectodconvergentes.R
import co.edu.unipiloto.proyectodconvergentes.ui.net.RetrofitModule
import co.edu.unipiloto.proyectodconvergentes.ui.net.TokenManager
import kotlinx.coroutines.launch

class RegisterDriverActivity : AppCompatActivity() {

    private val backendService by lazy { RetrofitModule.backendService(this) }
    private val tokenManager by lazy { TokenManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_driver)

        val email = findViewById<EditText>(R.id.inputEmail)
        val password = findViewById<EditText>(R.id.inputPassword)
        val name = findViewById<EditText>(R.id.inputName)
        val phone = findViewById<EditText>(R.id.inputPhone)
        val identification = findViewById<EditText>(R.id.inputIdentification)
        val license = findViewById<EditText>(R.id.inputLicense)
        val vehicle = findViewById<EditText>(R.id.inputVehicle)
        val hireDate = findViewById<EditText>(R.id.inputHireDate)
        val btnSubmit = findViewById<Button>(R.id.btnSubmitDriver)
        val progress = findViewById<ProgressBar>(R.id.progressRegisterDriver)

        btnSubmit.setOnClickListener {
            val emailText = email.text.toString().trim()
            val passwordText = password.text.toString().trim()
            val nameText = name.text.toString().trim()
            val phoneText = phone.text.toString().trim()
            val idText = identification.text.toString().trim()
            val licenseText = license.text.toString().trim()
            val vehicleText = vehicle.text.toString().trim()
            val hireDateText = hireDate.text.toString().trim()

            if (!validateFields(
                    emailText, passwordText, nameText, phoneText,
                    idText, licenseText, vehicleText, hireDateText
                )
            ) return@setOnClickListener

            val request = DriverRegisterRequest(
                email = emailText,
                password = passwordText,
                name = nameText,
                phoneNumber = phoneText,
                identification = idText,
                licenseNumber = licenseText,
                vehicleAssigned = vehicleText,
                hireDate = hireDateText
            )

            progress.visibility = View.VISIBLE
            lifecycleScope.launch {
                try {
                    val response = backendService.registerDriver(request)
                    progress.visibility = View.GONE
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@RegisterDriverActivity,
                            "Conductor registrado correctamente",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    } else {
                        val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                        Toast.makeText(
                            this@RegisterDriverActivity,
                            "Error: $errorMsg",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    progress.visibility = View.GONE
                    Toast.makeText(
                        this@RegisterDriverActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun validateFields(
        email: String,
        password: String,
        name: String,
        phone: String,
        identification: String,
        license: String,
        vehicle: String,
        hireDate: String
    ): Boolean {
        if (email.isEmpty() || password.isEmpty() || name.isEmpty() ||
            phone.isEmpty() || identification.isEmpty() ||
            license.isEmpty() || vehicle.isEmpty() || hireDate.isEmpty()
        ) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_LONG).show()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "El email no es válido", Toast.LENGTH_LONG).show()
            return false
        }

        // Validar formato de fecha YYYY-MM-DD
        val dateRegex = Regex("^\\d{4}-\\d{2}-\\d{2}$")
        if (!hireDate.matches(dateRegex)) {
            Toast.makeText(this, "La fecha debe tener el formato YYYY-MM-DD", Toast.LENGTH_LONG).show()
            return false
        }

        if (phone.length < 7) {
            Toast.makeText(this, "El número de teléfono no es válido", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }
}
