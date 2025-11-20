package co.edu.unipiloto.proyectodconvergentes.ui.user

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import co.edu.unipiloto.proyectodconvergentes.R
import co.edu.unipiloto.proyectodconvergentes.ui.net.RetrofitModule
import co.edu.unipiloto.proyectodconvergentes.ui.net.errorMessage
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class EditProfileActivity : AppCompatActivity() {

    private val backend by lazy { RetrofitModule.backendService(this) }

    private lateinit var etName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var progress: ProgressBar
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
        // Ícono del sistema, sin drawables externos
        toolbar.setNavigationIcon(android.R.drawable.ic_media_previous)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        progress = findViewById(R.id.progressBar)
        btnSave = findViewById(R.id.btnSave)

        // IMPORTANTE: No precargamos nada. Los campos quedan vacíos (solo hints).
        btnSave.setOnClickListener { submit() }
    }

    private fun submit() {
        val name = etName.text?.toString()?.trim().orEmpty().ifEmpty { null }
        val email = etEmail.text?.toString()?.trim().orEmpty().ifEmpty { null }
        val phone = etPhone.text?.toString()?.trim().orEmpty().ifEmpty { null }

        // Validación ligera: si se escribió email, que tenga formato válido
        if (email != null && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            toast("Correo inválido")
            return
        }

        // Si no escribió nada en ningún campo, no tiene sentido enviar
        if (name == null && email == null && phone == null) {
            toast("Escribe al menos un campo para actualizar")
            return
        }

        val request = UpdateProfileRequest(
            name = name,
            email = email,
            phoneNumber = phone
        )

        progress.visibility = View.VISIBLE
        btnSave.isEnabled = false

        lifecycleScope.launch {
            try {
                val response = backend.updateMyprofile(request)
                progress.visibility = View.GONE
                btnSave.isEnabled = true

                if (response.isSuccessful) {
                    toast("Perfil actualizado correctamente")
                    finish()
                } else {
                    toast(response.errorMessage())
                }
            } catch (e: Exception) {
                progress.visibility = View.GONE
                btnSave.isEnabled = true
                toast("Error: ${e.message}")
            }
        }
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
