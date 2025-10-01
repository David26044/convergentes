package co.edu.unipiloto.proyectodconvergentes.ui.order

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.edu.unipiloto.proyectodconvergentes.R

class DriverOrdersAdapter : RecyclerView.Adapter<DriverOrdersAdapter.DriverOrderViewHolder>() {

    private val orders = mutableListOf<OrderResponse>()

    fun submitList(newOrders: List<OrderResponse>) {
        orders.clear()
        orders.addAll(newOrders)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DriverOrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_driver, parent, false)
        return DriverOrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: DriverOrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size

    class DriverOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtOrderId: TextView = itemView.findViewById(R.id.txtDriverOrderId)
        private val txtState: TextView = itemView.findViewById(R.id.txtDriverOrderState)
        private val txtPickupAddress: TextView = itemView.findViewById(R.id.txtPickupAddress)
        private val txtDeliveryAddress: TextView = itemView.findViewById(R.id.txtDeliveryAddress)
        private val txtInstructions: TextView = itemView.findViewById(R.id.txtInstructions)
        private val txtAskFor: TextView = itemView.findViewById(R.id.txtAskFor)

        fun bind(order: OrderResponse) {
            txtOrderId.text = "Orden #${order.id}"
            txtState.text = "Estado: ${order.orderState.state}"

            // Dirección pickup
            val pickup = order.pickUpLocation
            txtPickupAddress.text = "Recoger en: ${pickup.city}, ${pickup.typeVia} ${pickup.numberVia}" +
                    (pickup.apartment?.let { ", Apt $it" } ?: "") +
                    (pickup.block?.let { ", Bloque $it" } ?: "")

            // Dirección delivery
            val delivery = order.deliveryLocation
            txtDeliveryAddress.text = "Entregar en: ${delivery.city}, ${delivery.typeVia} ${delivery.numberVia}" +
                    (delivery.apartment?.let { ", Apt $it" } ?: "") +
                    (delivery.block?.let { ", Bloque $it" } ?: "")

            // Instrucciones (del delivery, pero podrías también mostrar las de pickup)
            txtInstructions.text = "Instrucciones: ${delivery.instructions ?: "N/A"}"

            // Preguntar por
            txtAskFor.text = "Preguntar por: ${delivery.askFor ?: "N/A"}"
        }
    }
}
