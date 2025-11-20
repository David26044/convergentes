package co.edu.unipiloto.proyectodconvergentes.ui.net

import com.squareup.moshi.Moshi
import retrofit2.Response

fun Response<*>.errorMessage(): String {
    val code = this.code()
    val raw = this.errorBody()?.string()
    if (raw.isNullOrBlank()) return "Error $code"

    return try {
        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter(ErrorResponse::class.java)
        val parsed = adapter.fromJson(raw)
        buildString {
            append(parsed?.message ?: "Error $code")
            val violations = parsed?.violations.orEmpty()
            if (violations.isNotEmpty()) {
                append(violations.joinToString(prefix = "\n• ", separator = "\n• ") {
                    val f = it.field ?: "campo"
                    val m = it.message ?: "inválido"
                    "$f: $m"
                })
            }
        }
    } catch (_: Exception) {
        "Error $code: $raw"
    }
}
