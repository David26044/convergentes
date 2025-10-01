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
import co.edu.unipiloto.proyectodconvergentes.ui.net.RetrofitModule
import co.edu.unipiloto.proyectodconvergentes.ui.net.TokenManager
import co.edu.unipiloto.proyectodconvergentes.ui.net.JwtDecoder
import co.edu.unipiloto.proyectodconvergentes.ui.net.Roles
import co.edu.unipiloto.proyectodconvergentes.ui.net.hasAnyRole
import kotlinx.coroutines.launch

class GetMyOrdersActivity : AppCompatActivity() {

    private lateinit var recyclerOrders: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: OrdersAdapter

    private val tokenManager by lazy { TokenManager(this) }
    private val backendService by lazy { RetrofitModule.backendService(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_my_orders)

        recyclerOrders = findViewById(R.id.recyclerOrders)
        progressBar = findViewById(R.id.progressBar)

        adapter = OrdersAdapter(emptyList())
        recyclerOrders.layoutManager = LinearLayoutManager(this)
        recyclerOrders.adapter = adapter

        loadOrders()
    }

    private fun loadOrders() {
        progressBar.visibility = View.VISIBLE
        Log.d("GetMyOrdersActivity", "🚀 Cargando órdenes...")

        lifecycleScope.launch {
            try {
                val token = tokenManager.getToken()
                if (token.isNullOrBlank()) {
                    handleError("Token no encontrado")
                    return@launch
                }

                val claims = JwtDecoder.extractClaims(token)
                val roles = claims.roles
                Log.d("GetMyOrdersActivity", "🔑 Roles decodificados: $roles")

                val response = if (roles.hasAnyRole(Roles.ADMIN)) {
                    Log.d("GetMyOrdersActivity", "👑 Rol ADMIN → obteniendo todas las órdenes")
                    backendService.getAllOrders()
                } else {
                    Log.d("GetMyOrdersActivity", "🙋 Rol USER → obteniendo mis órdenes")
                    backendService.getMyOrders()
                }

                progressBar.visibility = View.GONE
                handleResponse(response.isSuccessful, response.body(), response.code(), response.errorBody()?.string(), roles)

            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Log.e("GetMyOrdersActivity", "💥 Excepción en loadOrders: ${e.message}", e)
                Toast.makeText(this@GetMyOrdersActivity, "Excepción: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun handleResponse(
        isSuccessful: Boolean,
        orders: List<OrderResponse>?,
        code: Int,
        errorMsg: String?,
        roles: List<String>
    ) {
        if (isSuccessful) {
            val safeOrders = orders.orEmpty()
            Log.d("GetMyOrdersActivity", "✅ Órdenes recibidas: ${safeOrders.size}")
            adapter.updateData(safeOrders)

            val msg = if (safeOrders.isEmpty()) {
                if (roles.hasAnyRole(Roles.ADMIN)) {
                    "No hay órdenes pendientes por asignar"
                } else {
                    "No tienes órdenes aún"
                }
            } else {
                "Órdenes cargadas correctamente"
            }
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        } else {
            Log.e("GetMyOrdersActivity", "❌ Error en API: $code - $errorMsg")
            Toast.makeText(
                this,
                "Error al cargar órdenes: $code",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun handleError(message: String) {
        progressBar.visibility = View.GONE
        Log.e("GetMyOrdersActivity", "❌ $message")
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
