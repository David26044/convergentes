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
        holder.tvOrderId.text = "Orden #${q.order.id}"
        holder.tvScore.text = "Puntuación: ${q.score}"
        holder.tvComment.text = q.comment ?: "(Sin comentario)"
    }

    fun updateData(newList: List<QualificationResponse>) {
        this.qualifications = newList
        notifyDataSetChanged()
    }
}
