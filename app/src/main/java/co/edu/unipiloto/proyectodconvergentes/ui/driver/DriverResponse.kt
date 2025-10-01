package co.edu.unipiloto.proyectodconvergentes.ui.driver

data class DriverResponse(
    val id: Long,
    val email: String?,
    val name: String?,
    val phoneNumber: String?,
    val identification: String?,
    val licenseNumber: String?,
    val vehicleAssigned: String?,
    val hireDate: String?
)
