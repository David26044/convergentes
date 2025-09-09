package co.edu.unipiloto.proyectodconvergentes.ui.net

import co.edu.unipiloto.proyectodconvergentes.ui.login.UserLoginRequest
import co.edu.unipiloto.proyectodconvergentes.ui.login.UserLoginResponse
import co.edu.unipiloto.proyectodconvergentes.ui.register.UserRegisterRequest
import co.edu.unipiloto.proyectodconvergentes.ui.register.UserRegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface BackendService {
    @POST(Constants.PATH_REGISTER)
    suspend fun registerNewUser(@Body userRequest: UserRegisterRequest): Response<UserRegisterResponse>

    @POST(Constants.PATH_LOGIN)
    suspend fun loginUser(@Body loginRequest: UserLoginRequest): Response<UserLoginResponse>

    @GET(Constants.PATH_GET_PAYMENT_METHODS)
    suspend fun getPaymentMethods(): Response<List<String>>
}
