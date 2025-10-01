package co.edu.unipiloto.proyectodconvergentes.ui.net

import android.util.Base64
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.StandardCharsets

object JwtDecoder {
    data class Claims(val subject: String?, val roles: List<String>, val expiresAtSeconds: Long?)

    private fun decodePayload(token: String): JSONObject? {
        val parts = token.split("."); if (parts.size < 2) return null
        val decoded = Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
        return JSONObject(String(decoded, StandardCharsets.UTF_8))
    }

    fun extractClaims(token: String): Claims {
        val json = decodePayload(token) ?: return Claims(null, emptyList(), null)
        val sub = json.optString("sub", null)
        val exp = if (json.has("exp")) json.optLong("exp") else null
        val roles = when {
            json.has("authorities") -> json.optJSONArray("authorities")?.toStringListByKey("authority").orEmpty()
            else -> emptyList()
        }
        return Claims(sub, roles, exp)
    }

    fun isExpired(token: String, skewSeconds: Long = 30): Boolean {
        val exp = extractClaims(token).expiresAtSeconds ?: return true
        val nowSec = System.currentTimeMillis() / 1000
        return nowSec >= (exp - skewSeconds)
    }

    private fun JSONArray.toStringListByKey(key: String): List<String> {
        val out = mutableListOf<String>()
        for (i in 0 until length()) out += getJSONObject(i).optString(key)
        return out
    }
}
