package co.edu.unipiloto.proyectodconvergentes.ui.driver

import java.time.LocalDate

data class DriverRegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val phoneNumber: String,
    val identification: String,
    val hireDate: String, // ⬅️ antes era LocalDate
    val licenseNumber: String,
    val vehicleAssigned: String
)
