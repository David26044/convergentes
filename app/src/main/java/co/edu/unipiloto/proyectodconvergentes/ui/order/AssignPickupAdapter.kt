package co.edu.unipiloto.proyectodconvergentes.ui.order

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.edu.unipiloto.proyectodconvergentes.R
import co.edu.unipiloto.proyectodconvergentes.ui.driver.DriverResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AssignPickupAdapter(
    private var orders: MutableList<OrderResponse>,
    private var drivers: List<DriverResponse>,
    private val onAssign: suspend (locationId: Long, assignDriver: AssignDriver) -> Unit
) : RecyclerView.Adapter<AssignPickupAdapter.AssignPickupViewHolder>() {

    inner class AssignPickupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOrderId: TextView = view.findViewById(R.id.tvOrderId)
        val tvProduct: TextView = view.findViewById(R.id.tvProduct)
        val tvPayment: TextView = view.findViewById(R.id.tvPayment)
        val tvDeclaredValue: TextView = view.findViewById(R.id.tvDeclaredValue)
        val tvPickup: TextView = view.findViewById(R.id.tvPickup)
        val spinnerPickup: Spinner = view.findViewById(R.id.spinnerPickupDriver)
        val btnAssignPickup: Button = view.findViewById(R.id.btnAssignDriver)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignPickupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_assign, parent, false)
        return AssignPickupViewHolder(view)
    }

    override fun getItemCount(): Int = orders.size

    override fun onBindViewHolder(holder: AssignPickupViewHolder, position: Int) {
        val order = orders[position]

        holder.tvOrderId.text = "Orden #${order.id}"
        holder.tvProduct.text = "Producto: ${order.productType?.name ?: "N/A"}"
        holder.tvPayment.text = "Pago: ${order.paymentMethod?.name ?: "N/A"}"
        holder.tvDeclaredValue.text = "Valor declarado: ${order.declaredValue ?: 0.0}"
        holder.tvPickup.text =
            "Recoger en: ${order.pickUpLocation.city} - ${order.pickUpLocation.typeVia} ${order.pickUpLocation.numberVia}"

        try {
            val tvDelivery = holder.itemView.findViewById<TextView>(R.id.tvDelivery)
            val spinnerDelivery = holder.itemView.findViewById<Spinner>(R.id.spinnerDeliveryDriver)
            tvDelivery?.visibility = View.GONE
            spinnerDelivery?.visibility = View.GONE
        } catch (e: Exception) {
            Log.d("AssignPickupAdapter", "No hay views de entrega, nada que ocultar")
        }

        // 👉 llenar spinner con conductores
        val context = holder.itemView.context
        val driverNames = drivers.map { it.name ?: "Sin nombre" }

        val pickupAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, driverNames)
        pickupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.spinnerPickup.adapter = pickupAdapter

        // 👉 asignar conductor al pickup
        holder.btnAssignPickup.setOnClickListener {
            val pickupDriverId = drivers[holder.spinnerPickup.selectedItemPosition].id
            val request = AssignDriver(pickupDriverId)

            CoroutineScope(Dispatchers.IO).launch {
                onAssign(order.pickUpLocation.id, request)
            }
        }

        // 🔍 Debug en Logcat
        Log.d("AssignPickupAdapter", "📦 Orden: id=${order.id}, producto=${order.productType?.name}, pago=${order.paymentMethod?.name}, valor=${order.declaredValue}, pickup=${order.pickUpLocation.city}")
    }

    fun updateData(newOrders: List<OrderResponse>, newDrivers: List<DriverResponse>) {
        orders.clear()
        orders.addAll(newOrders)
        drivers = newDrivers
        notifyDataSetChanged()
    }
}
