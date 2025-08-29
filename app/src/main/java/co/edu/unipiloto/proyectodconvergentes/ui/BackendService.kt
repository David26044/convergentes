package co.edu.unipiloto.proyectodconvergentes.ui

import co.edu.unipiloto.proyectodconvergentes.ui.register.UserRegisterRequest
import co.edu.unipiloto.proyectodconvergentes.ui.register.UserRegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BackendService {
    @POST(Constants.PATH_REGISTER)
    suspend fun registerNewUser(@Body userRequest: UserRegisterRequest): Response<UserRegisterResponse>
}
