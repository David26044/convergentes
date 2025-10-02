package co.edu.unipiloto.proyectodconvergentes.ui.qualification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.edu.unipiloto.proyectodconvergentes.R
import co.edu.unipiloto.proyectodconvergentes.ui.order.OrderResponse

class DeliveredOrdersAdapter(
    private var orders: List<OrderResponse>,
    private val onQualifyClick: (OrderResponse) -> Unit
) : RecyclerView.Adapter<DeliveredOrdersAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOrderId: TextView = view.findViewById(R.id.tvOrderId)
        val btnQualify: Button = view.findViewById(R.id.btnQualify)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_delivered_order_for_qualification, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = orders.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]
        holder.tvOrderId.text = "Orden #${order.id}"
        holder.btnQualify.setOnClickListener { onQualifyClick(order) }
    }

    fun updateData(newOrders: List<OrderResponse>) {
        this.orders = newOrders
        notifyDataSetChanged()
    }
}
