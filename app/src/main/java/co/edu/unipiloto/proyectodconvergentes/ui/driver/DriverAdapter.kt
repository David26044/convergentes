package co.edu.unipiloto.proyectodconvergentes.ui.driver

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.edu.unipiloto.proyectodconvergentes.R

class DriverAdapter(private val drivers: List<DriverResponse>) :
    RecyclerView.Adapter<DriverAdapter.DriverViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DriverViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_driver, parent, false)
        return DriverViewHolder(view)
    }

    override fun onBindViewHolder(holder: DriverViewHolder, position: Int) {
        val driver = drivers[position]
        holder.bind(driver)
    }

    override fun getItemCount(): Int = drivers.size

    class DriverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtName: TextView = itemView.findViewById(R.id.txtDriverName)
        private val txtEmail: TextView = itemView.findViewById(R.id.txtDriverEmail)
        private val txtPhone: TextView = itemView.findViewById(R.id.txtDriverPhone)
        private val txtIdentification: TextView = itemView.findViewById(R.id.txtDriverIdentification)
        private val txtHireDate: TextView = itemView.findViewById(R.id.txtDriverHireDate)
        private val txtLicense: TextView = itemView.findViewById(R.id.txtDriverLicense)
        private val txtVehicle: TextView = itemView.findViewById(R.id.txtDriverVehicle)

        fun bind(driver: DriverResponse) {
            txtName.text = "${driver.name ?: "N/A"}"
            txtEmail.text = "${driver.email ?: "N/A"}"
            txtPhone.text = "${driver.phoneNumber ?: "N/A"}"
            txtIdentification.text = "Identificación: ${driver.identification ?: "N/A"}"
            txtHireDate.text = "Ingreso: ${driver.hireDate ?: "N/A"}"
            txtLicense.text = "Licencia: ${driver.licenseNumber ?: "N/A"}"
            txtVehicle.text = "Vehículo: ${driver.vehicleAssigned ?: "N/A"}"
        }
    }
}
