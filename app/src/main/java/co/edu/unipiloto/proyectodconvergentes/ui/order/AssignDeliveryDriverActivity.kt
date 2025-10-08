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

class AssignDeliveryDriverActivity : AppCompatActivity() {

    private lateinit var recyclerAssignDeliveries: RecyclerView
    private lateinit var progressBarAssignDelivery: ProgressBar
    private lateinit var adapter: AssignDeliveryAdapter

    private val tokenManager by lazy { TokenManager(this) }
    private val backendService by lazy { RetrofitModule.backendService(this) }

    private var orders: MutableList<OrderResponse> = mutableListOf()
    private var drivers: List<DriverResponse> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assign_delivery_driver)

        recyclerAssignDeliveries = findViewById(R.id.recyclerAssignDeliveryOrders)
        progressBarAssignDelivery = findViewById(R.id.progressBarAssignDelivery)

        adapter = AssignDeliveryAdapter(orders, drivers) { locationId, request ->
            lifecycleScope.launch {
                assignDriver(locationId, request)
            }
        }

        recyclerAssignDeliveries.layoutManager = LinearLayoutManager(this)
        recyclerAssignDeliveries.adapter = adapter

        loadDriversAndOrders()
    }

    private fun loadDriversAndOrders() {
        progressBarAssignDelivery.visibility = View.VISIBLE
        Log.d("AssignDelivery", "🚀 Iniciando carga de conductores y órdenes...")

        lifecycleScope.launch {
            try {
                val token = tokenManager.getToken()
                Log.d("AssignDelivery", "🔑 Token obtenido: $token")

                if (token.isNullOrBlank()) {
                    Toast.makeText(this@AssignDeliveryDriverActivity, "Token no encontrado", Toast.LENGTH_SHORT).show()
                    progressBarAssignDelivery.visibility = View.GONE
                    return@launch
                }

                // 1️⃣ Cargar conductores
                val driversResponse: Response<List<DriverResponse>> = backendService.getAllDrivers()
                if (driversResponse.isSuccessful) {
                    drivers = driversResponse.body().orEmpty()
                    Log.d("AssignDelivery", "✅ Conductores cargados: ${drivers.size}")
                } else {
                    Log.e("AssignDelivery", "❌ Error al cargar drivers: ${driversResponse.code()} - ${driversResponse.errorBody()?.string()}")
                }

                // 2️⃣ Cargar órdenes (de momento, todas)
                val ordersResponse = backendService.getAllOrders()
                progressBarAssignDelivery.visibility = View.GONE

                if (ordersResponse.isSuccessful) {
                    val newOrders = ordersResponse.body().orEmpty()
                    orders.clear()
                    orders.addAll(newOrders)

                    adapter.updateData(newOrders, drivers)
                    Log.d("AssignDelivery", "✅ Órdenes cargadas: ${newOrders.size}")
                } else {
                    Log.e("AssignDelivery", "❌ Error API órdenes: ${ordersResponse.code()} - ${ordersResponse.errorBody()?.string()}")
                }

            } catch (e: Exception) {
                progressBarAssignDelivery.visibility = View.GONE
                Log.e("AssignDelivery", "💥 Excepción en loadDriversAndOrders: ${e.message}", e)
            }
        }
    }

    private suspend fun assignDriver(locationId: Long, request: AssignDriver) {
        try {
            Log.d("AssignDelivery", "👉 PATCH /locations/$locationId/assign-driver con body: $request")

            val response = backendService.assignDriver(locationId, request)

            if (response.isSuccessful) {
                Log.d("AssignDelivery", "✅ Conductor de entrega asignado correctamente: ${response.body()}")
                runOnUiThread {
                    Toast.makeText(this@AssignDeliveryDriverActivity, "Conductor asignado a la entrega #$locationId", Toast.LENGTH_SHORT).show()
                }
                loadDriversAndOrders()
            } else {
                val msg = response.errorBody()?.string().orEmpty()
                Log.e("AssignDelivery", "❌ Error (${response.code()}): $msg")
            }
        } catch (e: Exception) {
            Log.e("AssignDelivery", "💥 Excepción en assignDriver: ${e.message}", e)
        }
    }
}
