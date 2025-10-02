package co.edu.unipiloto.proyectodconvergentes.ui.order

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

class AssignDeliveryAdapter(
    private var orders: MutableList<OrderResponse>,
    private var drivers: List<DriverResponse>,
    private val onAssign: suspend (locationId: Long, assignDriver: AssignDriver) -> Unit
) : RecyclerView.Adapter<AssignDeliveryAdapter.AssignDeliveryViewHolder>() {

    inner class AssignDeliveryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOrderId: TextView = view.findViewById(R.id.tvOrderId)
        val tvProduct: TextView = view.findViewById(R.id.tvProduct)
        val tvPayment: TextView = view.findViewById(R.id.tvPayment)
        val tvDeclaredValue: TextView = view.findViewById(R.id.tvDeclaredValue)
        val tvDelivery: TextView = view.findViewById(R.id.tvDelivery)
        val spinnerDelivery: Spinner = view.findViewById(R.id.spinnerDeliveryDriver)
        val btnAssignDelivery: Button = view.findViewById(R.id.btnAssignDeliveryDriver)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignDeliveryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_assign_delivery, parent, false)
        return AssignDeliveryViewHolder(view)
    }

    override fun getItemCount(): Int = orders.size

    override fun onBindViewHolder(holder: AssignDeliveryViewHolder, position: Int) {
        val order = orders[position]

        holder.tvOrderId.text = "Orden #${order.id}"
        holder.tvProduct.text = "Producto: ${order.productType?.name ?: "N/A"}"
        holder.tvPayment.text = "Pago: ${order.paymentMethod.name}"

        holder.tvDeclaredValue.text = "Valor declarado: ${order.declaredValue ?: 0.0}"
        holder.tvDelivery.text =
            "Entregar en: ${order.deliveryLocation.city} - ${order.deliveryLocation.typeVia} ${order.deliveryLocation.numberVia}"

        // 👉 llenar spinner con conductores
        val context = holder.itemView.context
        val driverNames = drivers.map { it.name ?: "Sin nombre" }

        val deliveryAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, driverNames)
        deliveryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.spinnerDelivery.adapter = deliveryAdapter

        // 👉 asignar conductor de entrega
        holder.btnAssignDelivery.setOnClickListener {
            val deliveryDriverId = drivers[holder.spinnerDelivery.selectedItemPosition].id
            val request = AssignDriver(deliveryDriverId)

            CoroutineScope(Dispatchers.IO).launch {
                onAssign(order.deliveryLocation.id, request)
            }
        }
    }

    fun updateData(newOrders: List<OrderResponse>, newDrivers: List<DriverResponse>) {
        orders.clear()
        // Filtrar solo las órdenes con estado EN_HUB
        orders.addAll(newOrders.filter { it.orderState.state == "EN_HUB" })
        drivers = newDrivers
        notifyDataSetChanged()
    }

}
