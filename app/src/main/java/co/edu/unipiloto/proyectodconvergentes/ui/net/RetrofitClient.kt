package co.edu.unipiloto.proyectodconvergentes.ui.net

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // USO: val auth = RetrofitClient.authService
    val authService: BackendService by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BackendService::class.java)
    }
}
