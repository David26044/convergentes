package co.edu.unipiloto.proyectodconvergentes.ui.order

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.edu.unipiloto.proyectodconvergentes.R
import co.edu.unipiloto.proyectodconvergentes.ui.driver.DriverResponse
import co.edu.unipiloto.proyectodconvergentes.ui.net.RetrofitModule
import co.edu.unipiloto.proyectodconvergentes.ui.net.TokenManager
import kotlinx.coroutines.launch
import retrofit2.Response

class AssignPickupDriverActivity : AppCompatActivity() {

    private lateinit var recyclerAssignPickups: RecyclerView
    private lateinit var progressBarAssign: ProgressBar
    private lateinit var adapter: AssignPickupAdapter

    private val tokenManager by lazy { TokenManager(this) }
    private val backendService by lazy { RetrofitModule.backendService(this) }

    private var orders: MutableList<OrderResponse> = mutableListOf()
    private var drivers: List<DriverResponse> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assign_driver)

        recyclerAssignPickups = findViewById(R.id.recyclerAssignOrders)
        progressBarAssign = findViewById(R.id.progressBarAssign)

        adapter = AssignPickupAdapter(orders, drivers) { locationId, request ->
            lifecycleScope.launch {
                assignDriver(locationId, request)
            }
        }

        recyclerAssignPickups.layoutManager = LinearLayoutManager(this)
        recyclerAssignPickups.adapter = adapter

        loadDriversAndOrders()
    }

    private fun loadDriversAndOrders() {
        progressBarAssign.visibility = View.VISIBLE
        Log.d("AssignPickup", "🚀 Iniciando carga de conductores y órdenes...")

        lifecycleScope.launch {
            try {
                val token = tokenManager.getToken()
                Log.d("AssignPickup", "🔑 Token obtenido: $token")

                if (token.isNullOrBlank()) {
                    Toast.makeText(this@AssignPickupDriverActivity, "Token no encontrado", Toast.LENGTH_SHORT).show()
                    progressBarAssign.visibility = View.GONE
                    return@launch
                }

                // 1️⃣ Cargar conductores
                val driversResponse: Response<List<DriverResponse>> = backendService.getAllDrivers()
                if (driversResponse.isSuccessful) {
                    drivers = driversResponse.body().orEmpty()
                    Log.d("AssignPickup", "✅ Conductores cargados: ${drivers.size}")
                } else {
                    Log.e("AssignPickup", "❌ Error al cargar drivers: ${driversResponse.code()} - ${driversResponse.errorBody()?.string()}")
                }

                // 2️⃣ Cargar órdenes sin conductor de recogida
                val ordersResponse = backendService.getOrdersWithOutDriver()
                progressBarAssign.visibility = View.GONE

                if (ordersResponse.isSuccessful) {
                    val newOrders = ordersResponse.body().orEmpty()
                    orders.clear()
                    orders.addAll(newOrders)

                    adapter.updateData(newOrders, drivers)
                    Log.d("AssignPickup", "✅ Órdenes sin conductor de recogida: ${newOrders.size}")
                } else {
                    Log.e("AssignPickup", "❌ Error API órdenes: ${ordersResponse.code()} - ${ordersResponse.errorBody()?.string()}")
                }

            } catch (e: Exception) {
                progressBarAssign.visibility = View.GONE
                Log.e("AssignPickup", "💥 Excepción en loadDriversAndOrders: ${e.message}", e)
            }
        }
    }

    private suspend fun assignDriver(locationId: Long, request: AssignDriver) {
        try {
            Log.d("AssignPickup", "👉 PATCH /locations/$locationId/assign-driver con body: $request")

            val response = backendService.assignDriver(locationId, request)

            if (response.isSuccessful) {
                Log.d("AssignPickup", "✅ Conductor asignado correctamente: ${response.body()}")
                runOnUiThread {
                    Toast.makeText(this@AssignPickupDriverActivity, "Conductor asignado a la recogida #$locationId", Toast.LENGTH_SHORT).show()
                }
                loadDriversAndOrders()
            } else {
                val msg = response.errorBody()?.string().orEmpty()
                Log.e("AssignPickup", "❌ Error (${response.code()}): $msg")
            }
        } catch (e: Exception) {
            Log.e("AssignPickup", "💥 Excepción en assignDriver: ${e.message}", e)
        }
    }
}
