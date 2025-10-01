package co.edu.unipiloto.proyectodconvergentes.ui.order

data class PaymentMethodResponse(
    val id: Long,
    val name: String,
    val descrption: String // Ojo: en el backend tienes "descrption" (sin i)
)
