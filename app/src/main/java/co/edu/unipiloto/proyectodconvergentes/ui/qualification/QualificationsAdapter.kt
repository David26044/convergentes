package co.edu.unipiloto.proyectodconvergentes.ui.qualification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.edu.unipiloto.proyectodconvergentes.R

class QualificationsAdapter(
    private var qualifications: List<QualificationResponse>
) : RecyclerView.Adapter<QualificationsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOrderId: TextView = view.findViewById(R.id.tvOrderId)
        val tvProductType: TextView = view.findViewById(R.id.tvProductType)
        val tvPaymentMethod: TextView = view.findViewById(R.id.tvPaymentMethod)
        val tvRemitent: TextView = view.findViewById(R.id.tvRemitent)
        val tvDeliveryLocation: TextView = view.findViewById(R.id.tvDeliveryLocation)
        val tvDeliveryDate: TextView = view.findViewById(R.id.tvDeliveryDate)
        val tvScore: TextView = view.findViewById(R.id.tvScore)
        val tvComment: TextView = view.findViewById(R.id.tvComment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_qualification, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = qualifications.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val q = qualifications[position]
        val order = q.order

        holder.tvOrderId.text = "Orden #${order.id}"
        holder.tvProductType.text = "Producto: ${order.productType.name}"
        holder.tvPaymentMethod.text = "Pago: ${order.paymentMethod.name} - $${order.declaredValue}"
        holder.tvRemitent.text = "Remitente: ${order.remitent.name}"
        holder.tvDeliveryLocation.text = "Entrega: ${order.deliveryLocation.city}, ${order.deliveryLocation.typeVia} ${order.deliveryLocation.numberVia}"
        holder.tvDeliveryDate.text = "Fecha entrega: ${order.deliveryLocation.date ?: "N/A"}"

        holder.tvScore.text = "⭐ Puntuación: ${q.score}"
        holder.tvComment.text = q.comment ?: "(Sin comentario)"
    }

    fun updateData(newList: List<QualificationResponse>) {
        this.qualifications = newList
        notifyDataSetChanged()
    }
}
