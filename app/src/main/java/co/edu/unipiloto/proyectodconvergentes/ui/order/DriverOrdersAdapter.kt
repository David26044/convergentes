package co.edu.unipiloto.proyectodconvergentes.ui.order

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.edu.unipiloto.proyectodconvergentes.R

/**
 * Adaptador que muestra las órdenes del conductor.
 * Recibe una función que se ejecuta cuando el usuario toca el botón "Ir".
 */
class DriverOrdersAdapter(
    private val onNavigateClick: (OrderResponse) -> Unit = {},
    private val buttonLabel: String = "Ir"
) : RecyclerView.Adapter<DriverOrdersAdapter.DriverOrderViewHolder>() {

    // Lista interna de órdenes que se van a mostrar
    private val orders = mutableListOf<OrderResponse>()

    /**
     * Reemplaza el contenido actual por una nueva lista de órdenes.
     */
    fun submitList(newOrders: List<OrderResponse>) {
        orders.clear()
        orders.addAll(newOrders)
        notifyDataSetChanged() // Se le dice al RecyclerView que redibuje todo
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DriverOrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_driver, parent, false)
        return DriverOrderViewHolder(view, onNavigateClick, buttonLabel)
    }

    override fun onBindViewHolder(holder: DriverOrderViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    /**
     * ViewHolder: representa UNA fila (una orden) en la lista.
     */
    class DriverOrderViewHolder(
        itemView: View,
        private val onNavigateClick: (OrderResponse) -> Unit,
        private val buttonLabel: String
    ) : RecyclerView.ViewHolder(itemView) {

        private val txtOrderId: TextView = itemView.findViewById(R.id.txtDriverOrderId)
        private val txtState: TextView = itemView.findViewById(R.id.txtDriverOrderState)
        private val txtPickupAddress: TextView = itemView.findViewById(R.id.txtPickupAddress)
        private val txtDeliveryAddress: TextView = itemView.findViewById(R.id.txtDeliveryAddress)
        private val txtInstructions: TextView = itemView.findViewById(R.id.txtInstructions)
        private val txtAskFor: TextView = itemView.findViewById(R.id.txtAskFor)
        private val btnIr: Button = itemView.findViewById(R.id.btnDriverNavigate)

        /**
         * Rellena los textos de la vista con los datos de la orden.
         */
        fun bind(order: OrderResponse) {
            // ID y estado de la orden
            txtOrderId.text = "Orden #${order.id}"
            txtState.text = "Estado: ${order.orderState.state}"

            // Construir texto de dirección de recogida de forma simple
            val pickup = order.pickUpLocation
            val pickupBuilder = StringBuilder()
            pickupBuilder.append("Recoger en: ")
            pickupBuilder.append(pickup.city)
            pickupBuilder.append(", ")
            pickupBuilder.append(pickup.typeVia)
            pickupBuilder.append(" ")
            pickupBuilder.append(pickup.numberVia)

            // Agregar apartamento si no está vacío
            if (pickup.apartment != null && pickup.apartment.isNotBlank()) {
                pickupBuilder.append(", Apt ")
                pickupBuilder.append(pickup.apartment)
            }

            // Agregar bloque si no está vacío
            if (pickup.block != null && pickup.block.isNotBlank()) {
                pickupBuilder.append(", Bloque ")
                pickupBuilder.append(pickup.block)
            }

            txtPickupAddress.text = pickupBuilder.toString()

            // Construir texto de dirección de entrega
            val delivery = order.deliveryLocation
            val deliveryBuilder = StringBuilder()
            deliveryBuilder.append("Entregar en: ")
            deliveryBuilder.append(delivery.city)
            deliveryBuilder.append(", ")
            deliveryBuilder.append(delivery.typeVia)
            deliveryBuilder.append(" ")
            deliveryBuilder.append(delivery.numberVia)

            if (delivery.apartment != null && delivery.apartment.isNotBlank()) {
                deliveryBuilder.append(", Apt ")
                deliveryBuilder.append(delivery.apartment)
            }

            if (delivery.block != null && delivery.block.isNotBlank()) {
                deliveryBuilder.append(", Bloque ")
                deliveryBuilder.append(delivery.block)
            }

            txtDeliveryAddress.text = deliveryBuilder.toString()

            // Instrucciones y persona a preguntar
            val instructionsText =
                if (delivery.instructions != null && delivery.instructions.isNotBlank()) {
                    delivery.instructions
                } else {
                    "N/A"
                }
            txtInstructions.text = "Instrucciones: $instructionsText"

            val askForText =
                if (delivery.askFor != null && delivery.askFor.isNotBlank()) {
                    delivery.askFor
                } else {
                    "N/A"
                }
            txtAskFor.text = "Preguntar por: $askForText"

            // Configurar botón "Ir"
            btnIr.text = buttonLabel
            btnIr.setOnClickListener {
                // Cuando se toca el botón, se ejecuta la función pasada desde la Activity
                onNavigateClick(order)
            }
        }
    }
}
