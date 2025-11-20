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
import co.edu.unipiloto.proyectodconvergentes.ui.net.JwtDecoder
import co.edu.unipiloto.proyectodconvergentes.ui.net.RetrofitModule
import co.edu.unipiloto.proyectodconvergentes.ui.net.Roles
import co.edu.unipiloto.proyectodconvergentes.ui.net.TokenManager
import co.edu.unipiloto.proyectodconvergentes.ui.net.hasAnyRole
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch
import retrofit2.Response

class GetMyOrdersActivity : AppCompatActivity() {

    private lateinit var recyclerOrders: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var chipGroupFilters: ChipGroup
    private lateinit var adapter: OrdersAdapter

    private val tokenManager by lazy { TokenManager(this) }
    private val backendService by lazy { RetrofitModule.backendService(this) }

    private var allOrders: List<OrderResponse> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_my_orders)

        recyclerOrders = findViewById(R.id.recyclerOrders)
        progressBar = findViewById(R.id.progressBar)
        chipGroupFilters = findViewById(R.id.chipGroupFilters)

        // ✅ INICIALIZA EL ADAPTER AQUÍ
        adapter = OrdersAdapter() // arranca vacío, luego se llena con updateData()

        recyclerOrders.layoutManager = LinearLayoutManager(this)
        recyclerOrders.adapter = adapter

        chipGroupFilters.setOnCheckedChangeListener { _, checkedId ->
            applyFilter(checkedId)
        }

        loadOrders()
    }

    private fun loadOrders() {
        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val token = tokenManager.getToken()
                if (token.isNullOrBlank()) {
                    showError("Token no encontrado")
                    progressBar.visibility = View.GONE
                    return@launch
                }

                val claims = JwtDecoder.extractClaims(token)
                val roles = claims.roles

                val response: Response<List<OrderResponse>> = if (roles.hasAnyRole(Roles.ADMIN)) {
                    backendService.getAllOrders()
                } else {
                    backendService.getMyOrders()
                }

                progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    allOrders = response.body().orEmpty()
                    adapter.updateData(allOrders)
                } else {
                    showError("Error al cargar órdenes: ${response.code()}")
                }

            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                showError("Excepción: ${e.message}")
            }
        }
    }

    private fun applyFilter(checkedId: Int) {
        val filtered = when (checkedId) {
            R.id.chipAll -> allOrders
            R.id.chipCreada -> allOrders.filter { it.orderState.state == "CREADA" }
            R.id.chipRecogida -> allOrders.filter { it.orderState.state == "RECOGIDA" }
            R.id.chipEnHub -> allOrders.filter { it.orderState.state == "EN_HUB" }
            R.id.chipEnCamino -> allOrders.filter { it.orderState.state == "EN_CAMINO" }
            R.id.chipEntregada -> allOrders.filter { it.orderState.state == "ENTREGADA" }
            else -> allOrders
        }
        adapter.updateData(filtered)
    }

    private fun showError(message: String) {
        Log.e("GetMyOrdersActivity", "❌ $message")
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
