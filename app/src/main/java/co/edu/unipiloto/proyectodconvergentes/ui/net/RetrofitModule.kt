package co.edu.unipiloto.proyectodconvergentes.ui.net

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitModule {
    // USO: val service = RetrofitModule.backendService(context)
    fun backendService(context: Context): BackendService {
        val tm = TokenManager(context)
        val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .addInterceptor(AuthInterceptor { tm.getToken() }) // mete Authorization salvo /auth/*
            .build()

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BackendService::class.java)
    }
}
