package co.edu.unipiloto.proyectodconvergentes.ui.register

data class UserRegisterRequest(val name: String,
                               val identification: String,
                               val phoneNumber: String,
                               val email: String,
                               val password: String)
