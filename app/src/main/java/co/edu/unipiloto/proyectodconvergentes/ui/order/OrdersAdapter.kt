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

        holder.tvOrderId.text = String.format("Orden #%d", order.id)
        holder.tvProduct.text = String.format("Producto: %s", order.productType?.name ?: "N/A")
        holder.tvPayment.text = String.format("Pago: %s", order.paymentMethod?.name ?: "N/A")
        holder.tvDeclaredValue.text = String.format("Valor declarado: %.2f", order.declaredValue ?: 0.0)

        holder.tvPickup.text = String.format(
            "Recoger en: %s - %s %s",
            order.pickUpLocation.city,
            order.pickUpLocation.typeVia,
            order.pickUpLocation.numberVia
        )
        holder.tvPickupDriver.text = String.format(
            "Conductor recogida: %s",
            order.pickUpLocation.driver?.name ?: "Sin asignar"
        )

        holder.tvDelivery.text = String.format(
            "Entregar en: %s - %s %s",
            order.deliveryLocation.city,
            order.deliveryLocation.typeVia,
            order.deliveryLocation.numberVia
        )
        holder.tvDeliveryDriver.text = String.format(
            "Conductor entrega: %s",
            order.deliveryLocation.driver?.name ?: "Sin asignar"
        )

        holder.tvOrderState.text = String.format(
            "Estado: %s",
            order.orderState?.state ?: "Desconocido"
        )

        // Log para debug
        Log.d(
            "OrdersAdapter",
            "📦 Orden ${order.id}: producto=${order.productType?.name}, pago=${order.paymentMethod?.name}, estado=${order.orderState?.state}, pickupDriver=${order.pickUpLocation.driver?.name}, deliveryDriver=${order.deliveryLocation.driver?.name}"
        )
    }

    fun updateData(newOrders: List<OrderResponse>) {
        orders = newOrders
        notifyDataSetChanged()
        Log.d("OrdersAdapter", "🔄 Adapter actualizado con ${newOrders.size} órdenes")
    }
}
