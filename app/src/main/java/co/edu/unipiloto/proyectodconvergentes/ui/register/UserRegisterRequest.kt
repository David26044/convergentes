package co.edu.unipiloto.proyectodconvergentes.ui.register

data class UserRegisterRequest(val email: String,
                               val password: String,
                               val name: String,
                               val phoneNumber: String,
                               val identification: String)
