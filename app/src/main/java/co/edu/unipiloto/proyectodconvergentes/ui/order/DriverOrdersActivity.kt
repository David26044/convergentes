package co.edu.unipiloto.proyectodconvergentes.ui.order

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.edu.unipiloto.proyectodconvergentes.R
import co.edu.unipiloto.proyectodconvergentes.ui.net.RetrofitModule
import co.edu.unipiloto.proyectodconvergentes.ui.net.TokenManager
import kotlinx.coroutines.launch

class DriverOrdersActivity : AppCompatActivity() {
    private lateinit var recyclerPickup: RecyclerView
    private lateinit var recyclerDelivery: RecyclerView
    private lateinit var adapterPickup: DriverOrdersAdapter
    private lateinit var adapterDelivery: DriverOrdersAdapter
    private val tokenManager by lazy { TokenManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_orders)

        recyclerPickup = findViewById(R.id.recyclerPickupOrders)
        recyclerDelivery = findViewById(R.id.recyclerDeliveryOrders)

        recyclerPickup.layoutManager = LinearLayoutManager(this)
        recyclerDelivery.layoutManager = LinearLayoutManager(this)

        adapterPickup = DriverOrdersAdapter()
        adapterDelivery = DriverOrdersAdapter()

        recyclerPickup.adapter = adapterPickup
        recyclerDelivery.adapter = adapterDelivery

        loadOrders()
    }

    private fun loadOrders() {
        lifecycleScope.launch {
            try {
                val service = RetrofitModule.backendService(this@DriverOrdersActivity)
                val response = service.getOrdersByDriver()

                if (response.isSuccessful) {
                    val orders = response.body() ?: emptyList()

                    // Obtener el email del driver logueado desde el token
                    val token = tokenManager.getToken()
                    val claims = token?.let { co.edu.unipiloto.proyectodconvergentes.ui.net.JwtDecoder.extractClaims(it) }
                    val email = claims?.subject

                    // 🔹 Pickup: solo órdenes del driver y en estado CREADA
                    val pickupOrders = orders.filter {
                        it.pickUpLocation.driver?.email == email &&
                                it.orderState.state == "CREADA"
                    }

                    // 🔹 Delivery: solo órdenes del driver y en estado EN_CAMINO
                    val deliveryOrders = orders.filter {
                        it.deliveryLocation.driver?.email == email &&
                                it.orderState.state == "EN_CAMINO"
                    }

                    adapterPickup.submitList(pickupOrders)
                    adapterDelivery.submitList(deliveryOrders)

                } else {
                    Toast.makeText(this@DriverOrdersActivity, "Error cargando órdenes", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@DriverOrdersActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

