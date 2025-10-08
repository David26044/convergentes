package co.edu.unipiloto.proyectodconvergentes.ui.order

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.edu.unipiloto.proyectodconvergentes.R

class OrdersAdapter(
    private var orders: List<OrderResponse>
) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOrderId: TextView = view.findViewById(R.id.tvOrderId)
        val tvProduct: TextView = view.findViewById(R.id.tvProduct)
        val tvPayment: TextView = view.findViewById(R.id.tvPayment)
        val tvDeclaredValue: TextView = view.findViewById(R.id.tvDeclaredValue)
        val tvPickup: TextView = view.findViewById(R.id.tvPickup)
        val tvPickupDriver: TextView = view.findViewById(R.id.tvPickupDriver)
        val tvDelivery: TextView = view.findViewById(R.id.tvDelivery)
        val tvDeliveryDriver: TextView = view.findViewById(R.id.tvDeliveryDriver)
        val tvOrderState: TextView = view.findViewById(R.id.tvOrderState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun getItemCount(): Int = orders.size

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]

        holder.tvOrderId.text = "Orden #${order.id}"
        holder.tvProduct.text = "Producto: ${order.productType.name}"
        holder.tvPayment.text = "Pago: ${order.paymentMethod.name}"
        holder.tvDeclaredValue.text = "Valor declarado: ${order.declaredValue}"

        holder.tvPickup.text = "Recoger en: ${order.pickUpLocation.city} - ${order.pickUpLocation.typeVia} ${order.pickUpLocation.numberVia}"
        holder.tvPickupDriver.text = "Conductor recogida: ${order.pickUpLocation.driver?.name ?: "Sin asignar"}"

        holder.tvDelivery.text = "Entregar en: ${order.deliveryLocation.city} - ${order.deliveryLocation.typeVia} ${order.deliveryLocation.numberVia}"
        holder.tvDeliveryDriver.text = "Conductor entrega: ${order.deliveryLocation.driver?.name ?: "Sin asignar"}"

        holder.tvOrderState.text = "Estado: ${order.orderState.state}"

        Log.d("OrdersAdapter", "📦 Orden ${order.id} → Estado=${order.orderState.state}")
    }

    fun updateData(newOrders: List<OrderResponse>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}
