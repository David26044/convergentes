package co.edu.unipiloto.proyectodconvergentes.ui.net

// --- Errores del backend ---
data class ApiError(
    val timestamp: String? = null,
    val status: Int? = null,
    val error: String? = null,
    val code: String? = null,
    val message: String? = null,
    val path: String? = null,
    val fieldErrors: List<FieldError>? = null
)

data class FieldError(
    val field: String? = null,
    val message: String? = null,
    val rejectedValue: String? = null
)
