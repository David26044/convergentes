package co.edu.unipiloto.proyectodconvergentes.ui.order

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.edu.unipiloto.proyectodconvergentes.R
import co.edu.unipiloto.proyectodconvergentes.ui.net.RetrofitModule
import kotlinx.coroutines.launch

class DriverOrdersByStateActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: DriverOrdersByStateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_orders_by_state)

        recycler = findViewById(R.id.recyclerOrdersByState)
        recycler.layoutManager = LinearLayoutManager(this)

        adapter = DriverOrdersByStateAdapter(
            onNextStateClick = { order -> moveToNextState(order) }
        )
        recycler.adapter = adapter

        loadOrders()
    }

    private fun loadOrders() {
        lifecycleScope.launch {
            try {
                val service = RetrofitModule.backendService(this@DriverOrdersByStateActivity)
                val response = service.getOrdersByDriver()

                if (response.isSuccessful) {
                    val orders = response.body() ?: emptyList()
                    // Agrupar por estado y ordenar
                    val grouped = orders.groupBy { it.orderState.state }
                    adapter.submitData(grouped)
                } else {
                    Toast.makeText(this@DriverOrdersByStateActivity, "Error al cargar órdenes", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@DriverOrdersByStateActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun moveToNextState(order: OrderResponse) {
        lifecycleScope.launch {
            try {
                val service = RetrofitModule.backendService(this@DriverOrdersByStateActivity)
                val response = service.orderNextState(order.id)
                if (response.isSuccessful) {
                    Toast.makeText(this@DriverOrdersByStateActivity, "Orden #${order.id} cambió de estado", Toast.LENGTH_SHORT).show()
                    loadOrders() // refrescar lista
                } else {
                    Toast.makeText(this@DriverOrdersByStateActivity, "No se pudo cambiar el estado", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@DriverOrdersByStateActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
