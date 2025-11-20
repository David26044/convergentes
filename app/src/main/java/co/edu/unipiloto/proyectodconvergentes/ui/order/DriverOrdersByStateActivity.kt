package co.edu.unipiloto.proyectodconvergentes.ui.order

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

        requestNotificationPermission()

        recycler = findViewById(R.id.recyclerOrdersByState)
        recycler.layoutManager = LinearLayoutManager(this)

        adapter = DriverOrdersByStateAdapter(
            onNextStateClick = { order ->
                moveToNextState(order)
            }
        )
        recycler.adapter = adapter

        loadOrders()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }

    private fun loadOrders() {
        lifecycleScope.launch {
            try {
                val service = RetrofitModule.backendService(this@DriverOrdersByStateActivity)
                val response = service.getOrdersByDriver()

                if (response.isSuccessful) {
                    val orders = response.body() ?: emptyList()
                    val grouped = orders.groupBy { it.orderState.state }
                    adapter.submitData(grouped)
                } else {
                    Toast.makeText(
                        this@DriverOrdersByStateActivity,
                        "Error al cargar órdenes",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@DriverOrdersByStateActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun moveToNextState(order: OrderResponse) {
        lifecycleScope.launch {
            try {
                val service = RetrofitModule.backendService(this@DriverOrdersByStateActivity)
                val response = service.orderNextState(order.id)

                if (response.isSuccessful) {
                    val updatedOrder = response.body() ?: order
                    val newState = updatedOrder.orderState.state

                    Toast.makeText(
                        this@DriverOrdersByStateActivity,
                        "Orden #${order.id} cambió a $newState",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Enviar notificación según el estado
                    when (newState) {
                        "EN_DESTINO" -> {
                            // Simula notificación al destinatario
                            sendOrderNotification(
                                updatedOrder,
                                title = "Encomienda en destino",
                                message = "La encomienda #${updatedOrder.id} llegó a la ciudad de destino."
                            )
                        }
                        "ENTREGADA" -> {
                            // Simula notificación al remitente
                            sendOrderNotification(
                                updatedOrder,
                                title = "Encomienda entregada",
                                message = "La encomienda #${updatedOrder.id} fue entregada al destinatario."
                            )
                        }
                    }

                    loadOrders()

                } else {
                    Toast.makeText(
                        this@DriverOrdersByStateActivity,
                        "No se pudo cambiar el estado",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@DriverOrdersByStateActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun sendOrderNotification(order: OrderResponse, title: String, message: String) {
        val intent = Intent(this, OrderNotificationService::class.java).apply {
            putExtra(OrderNotificationService.EXTRA_TITLE, title)
            putExtra(OrderNotificationService.EXTRA_MESSAGE, message)
            putExtra(OrderNotificationService.EXTRA_ORDER_ID, order.id.toString())
        }
        startService(intent)
    }
}
