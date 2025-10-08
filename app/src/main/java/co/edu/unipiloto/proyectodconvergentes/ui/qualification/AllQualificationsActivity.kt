package co.edu.unipiloto.proyectodconvergentes.ui.qualification

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.edu.unipiloto.proyectodconvergentes.R
import co.edu.unipiloto.proyectodconvergentes.ui.net.RetrofitModule
import co.edu.unipiloto.proyectodconvergentes.ui.net.TokenManager
import kotlinx.coroutines.launch

class AllQualificationsActivity : AppCompatActivity() {

    private lateinit var recyclerQualifications: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: QualificationsAdapter

    private val tokenManager by lazy { TokenManager(this) }
    private val backendService by lazy { RetrofitModule.backendService(this) }

    private var qualifications: MutableList<QualificationResponse> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_qualifications)

        recyclerQualifications = findViewById(R.id.recyclerQualifications)
        progressBar = findViewById(R.id.progressBarQualifications)

        adapter = QualificationsAdapter(qualifications)
        recyclerQualifications.layoutManager = LinearLayoutManager(this)
        recyclerQualifications.adapter = adapter

        loadQualifications()
    }

    private fun loadQualifications() {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val response = backendService.getAllQualifications()
                progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val newList = response.body().orEmpty()
                    qualifications.clear()
                    qualifications.addAll(newList)
                    adapter.updateData(newList)
                } else {
                    Log.e("AllQualifications", "❌ Error: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Log.e("AllQualifications", "💥 Excepción: ${e.message}", e)
            }
        }
    }
}
