package co.edu.unipiloto.proyectodconvergentes.ui.qualification

data class RegisterQualificationRequest(
    val orderId: Long,
    val score: Int,
    val comment: String?
)
