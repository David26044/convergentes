package co.edu.unipiloto.proyectodconvergentes.ui.user

data class UpdateProfileRequest(
    val name: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null
)
