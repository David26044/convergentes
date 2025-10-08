package co.edu.unipiloto.proyectodconvergentes.ui.qualification

import android.content.Intent
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
import co.edu.unipiloto.proyectodconvergentes.ui.net.RetrofitModule
import co.edu.unipiloto.proyectodconvergentes.ui.net.TokenManager
import co.edu.unipiloto.proyectodconvergentes.ui.order.OrderResponse
import kotlinx.coroutines.launch
import retrofit2.Response

class GetDeliveredOrdersForQualificationActivity : AppCompatActivity() {

    private lateinit var recyclerDeliveredOrders: RecyclerView
    private lateinit var progressBarDelivered: ProgressBar
    private lateinit var adapter: DeliveredOrdersAdapter

    private val tokenManager by lazy { TokenManager(this) }
    private val backendService by lazy { RetrofitModule.backendService(this) }

    private var orders: MutableList<OrderResponse> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_delivered_orders_for_qualification)

        recyclerDeliveredOrders = findViewById(R.id.recyclerDeliveredOrders)
        progressBarDelivered = findViewById(R.id.progressBarDelivered)

        adapter = DeliveredOrdersAdapter(orders) { order ->
            val intent = Intent(this, CreateQualificationActivity::class.java)
            intent.putExtra("orderId", order.id)
            startActivity(intent)
        }

        recyclerDeliveredOrders.layoutManager = LinearLayoutManager(this)
        recyclerDeliveredOrders.adapter = adapter

        loadDeliveredOrders()
    }

    private fun loadDeliveredOrders() {
        progressBarDelivered.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val token = tokenManager.getToken()
                if (token.isNullOrBlank()) {
                    Toast.makeText(this@GetDeliveredOrdersForQualificationActivity, "Token no encontrado", Toast.LENGTH_SHORT).show()
                    progressBarDelivered.visibility = View.GONE
                    return@launch
                }

                val ordersResponse: Response<List<OrderResponse>> = backendService.getOrdersDelivered()
                progressBarDelivered.visibility = View.GONE

                if (ordersResponse.isSuccessful) {
                    val newOrders = ordersResponse.body().orEmpty()
                    orders.clear()
                    orders.addAll(newOrders)
                    adapter.updateData(newOrders)
                } else {
                    Log.e("DeliveredOrders", "❌ Error API: ${ordersResponse.code()} - ${ordersResponse.errorBody()?.string()}")
                }

            } catch (e: Exception) {
                progressBarDelivered.visibility = View.GONE
                Log.e("DeliveredOrders", "💥 Excepción: ${e.message}", e)
            }
        }
    }
}
