package co.edu.unipiloto.proyectodconvergentes.ui.driver

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.edu.unipiloto.proyectodconvergentes.R
import co.edu.unipiloto.proyectodconvergentes.ui.net.RetrofitModule
import kotlinx.coroutines.launch

class ViewDriversActivity : AppCompatActivity() {

    private val backendService by lazy { RetrofitModule.backendService(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_drivers)

        val recycler = findViewById<RecyclerView>(R.id.recyclerDrivers)
        val progress = findViewById<ProgressBar>(R.id.progressViewDrivers)

        recycler.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            progress.visibility = View.VISIBLE
            try {
                val response = backendService.getAllDrivers()
                progress.visibility = View.GONE
                if (response.isSuccessful) {
                    val drivers = response.body().orEmpty()
                    recycler.adapter = DriverAdapter(drivers)
                } else {
                    Toast.makeText(this@ViewDriversActivity, "Error al obtener conductores", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                progress.visibility = View.GONE
                Toast.makeText(this@ViewDriversActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
