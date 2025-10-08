package co.edu.unipiloto.proyectodconvergentes.ui.qualification

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import co.edu.unipiloto.proyectodconvergentes.R
import co.edu.unipiloto.proyectodconvergentes.ui.net.RetrofitModule
import co.edu.unipiloto.proyectodconvergentes.ui.net.TokenManager
import kotlinx.coroutines.launch

class CreateQualificationActivity : AppCompatActivity() {

    private val tokenManager by lazy { TokenManager(this) }
    private val backendService by lazy { RetrofitModule.backendService(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_qualification)

        val ratingBar = findViewById<RatingBar>(R.id.ratingBarScore)
        val etComment = findViewById<EditText>(R.id.etComment)
        val btnSave = findViewById<Button>(R.id.btnSaveQualification)

        val orderId = intent.getLongExtra("orderId", -1L)

        btnSave.setOnClickListener {
            val score = ratingBar.rating.toInt()
            val comment = etComment.text.toString()

            val request = RegisterQualificationRequest(orderId, score, comment)
            lifecycleScope.launch {
                try {
                    val response = backendService.createQualification(request)
                    if (response.isSuccessful) {
                        Toast.makeText(this@CreateQualificationActivity, "Calificación guardada", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Log.e("CreateQualification", "❌ Error: ${response.code()} - ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    Log.e("CreateQualification", "💥 Excepción: ${e.message}", e)
                }
            }
        }
    }
}
