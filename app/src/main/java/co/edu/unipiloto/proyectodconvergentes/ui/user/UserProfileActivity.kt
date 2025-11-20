package co.edu.unipiloto.proyectodconvergentes.ui.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import co.edu.unipiloto.proyectodconvergentes.R
import co.edu.unipiloto.proyectodconvergentes.ui.net.RetrofitModule
import co.edu.unipiloto.proyectodconvergentes.ui.net.errorMessage
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

class UserProfileActivity : AppCompatActivity() {

    private val backend by lazy { RetrofitModule.backendService(this) }

    private lateinit var progress: ProgressBar
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvIdentification: TextView
    private lateinit var btnEdit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        progress = findViewById(R.id.progressBar)
        tvName = findViewById(R.id.tvName)
        tvEmail = findViewById(R.id.tvEmail)
        tvPhone = findViewById(R.id.tvPhone)
        tvIdentification = findViewById(R.id.tvIdentification)
        btnEdit = findViewById(R.id.btnEdit)

        btnEdit.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadMe()
    }

    private fun loadMe() {
        progress.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val response = backend.getMe()
                progress.visibility = View.GONE
                if (response.isSuccessful) {
                    val me = response.body()
                    if (me != null) {
                        tvName.text = "Nombre: ${me.name}"
                        tvEmail.text = "Correo: ${me.email}"
                        tvPhone.text = "Teléfono: ${me.phoneNumber}"
                        tvIdentification.text = "Identificación: ${me.identification}"
                    } else {
                        toast("No se pudo cargar el perfil")
                    }
                } else {
                    toast(response.errorMessage())
                }
            } catch (e: Exception) {
                progress.visibility = View.GONE
                toast("Error: ${e.message}")
            }
        }
    }

    private fun toast(msg:String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
