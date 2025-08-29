package co.edu.unipiloto.proyectodconvergentes.ui

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient{
    private const val BASE_URL = Constants.BASE_URL

    val instance: BackendService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(BackendService::class.java)
    }
}