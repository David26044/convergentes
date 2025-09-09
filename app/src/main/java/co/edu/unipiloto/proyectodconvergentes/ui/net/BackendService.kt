package co.edu.unipiloto.proyectodconvergentes.ui.net

import co.edu.unipiloto.proyectodconvergentes.ui.register.UserRegisterRequest
import co.edu.unipiloto.proyectodconvergentes.ui.register.UserRegisterResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface BackendService {
    @POST(Constants.PATH_REGISTER)
    suspend fun registerNewUser(@Body userRequest: UserRegisterRequest): Response<UserRegisterResponse>

    @GET("payment-methods")
    fun getPaymentMethods(): Response<List<String>>
}