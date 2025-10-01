package co.edu.unipiloto.proyectodconvergentes.ui.order

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.edu.unipiloto.proyectodconvergentes.R

class DriverOrdersByStateAdapter(
    private val onNextStateClick: (OrderResponse) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<Any>() // puede ser String (header) o OrderResponse

    fun submitData(grouped: Map<String, List<OrderResponse>>) {
        items.clear()
        grouped.forEach { (state, orders) ->
            items.add(state)      // header
            items.addAll(orders)  // órdenes
        }
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position] is String) 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_order_state_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_order_driver_with_button, parent, false)
            OrderViewHolder(view, onNextStateClick)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            holder.bind(items[position] as String)
        } else if (holder is OrderViewHolder) {
            holder.bind(items[position] as OrderResponse)
        }
    }

    override fun getItemCount(): Int = items.size

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtHeader: TextView = itemView.findViewById(R.id.txtOrderStateHeader)
        fun bind(state: String) {
            txtHeader.text = "Estado: $state"
        }
    }

    class OrderViewHolder(
        itemView: View,
        private val onNextStateClick: (OrderResponse) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val txtOrderId: TextView = itemView.findViewById(R.id.txtDriverOrderId)
        private val txtPickup: TextView = itemView.findViewById(R.id.txtPickupAddress)
        private val txtDelivery: TextView = itemView.findViewById(R.id.txtDeliveryAddress)
        private val btnNextState: Button = itemView.findViewById(R.id.btnNextState)

        fun bind(order: OrderResponse) {
            txtOrderId.text = "Orden #${order.id}"
            txtPickup.text = "Recoger: ${order.pickUpLocation.city}, ${order.pickUpLocation.typeVia} ${order.pickUpLocation.numberVia}"
            txtDelivery.text = "Entregar: ${order.deliveryLocation.city}, ${order.deliveryLocation.typeVia} ${order.deliveryLocation.numberVia}"

            btnNextState.isEnabled = order.orderState.state != "ENTREGADA"
            btnNextState.setOnClickListener { onNextStateClick(order) }
        }
    }
}
