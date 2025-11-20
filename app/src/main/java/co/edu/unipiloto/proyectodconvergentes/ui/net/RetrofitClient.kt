package co.edu.unipiloto.proyectodconvergentes.ui.net

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // 🔹 Cliente base sin token (para login, registro, forgot password, etc.)
    private val okHttpClient = OkHttpClient.Builder().build()

    // 🔹 Instancia base de Retrofit
    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    // 🔹 Servicio principal (sin token)
    val authService: BackendService by lazy {
        retrofit.create(BackendService::class.java)
    }

    /**
     * ✅ Crea un servicio temporal con un token (por ejemplo, para reset de contraseña)
     * No guarda el token en el TokenManager ni reemplaza la sesión normal.
     */
    fun createAuthorizedService(resetToken: String): BackendService {
        // Crea un nuevo cliente HTTP que adjunta el token temporal
        val clientWithToken = okHttpClient.newBuilder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $resetToken")
                    .build()
                chain.proceed(request)
            }
            .build()

        // Crea una nueva instancia de Retrofit reutilizando la base existente
        val retrofitWithToken = retrofit.newBuilder()
            .client(clientWithToken)
            .build()

        // Devuelve un BackendService temporal que usará ese token
        return retrofitWithToken.create(BackendService::class.java)
    }
}
