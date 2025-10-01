package co.edu.unipiloto.proyectodconvergentes.ui.user

data class UserResponse(
    val id: Long,
    val name: String,
    val email: String?,
    val phoneNumber: String?,
    val identification: String?
)

