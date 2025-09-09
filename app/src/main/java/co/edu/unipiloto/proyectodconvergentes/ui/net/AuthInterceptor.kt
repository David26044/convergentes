package co.edu.unipiloto.proyectodconvergentes.ui.net

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenProvider: suspend () -> String?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val req0 = chain.request()
        val isAuth = req0.url.encodedPath.contains("/auth/")
        if (isAuth) return chain.proceed(req0)

        val token = runBlocking { tokenProvider() }
        val req = if (!token.isNullOrBlank())
            req0.newBuilder().addHeader("Authorization", "Bearer $token").build()
        else req0
        return chain.proceed(req)
    }
}
