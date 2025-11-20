package co.edu.unipiloto.proyectodconvergentes.ui.net

data class FieldViolation(
    val field: String? = null,
    val message: String? = null,
    val rejectedValue: Any? = null
)

data class ErrorResponse(
    val message: String? = null,
    val violations: List<FieldViolation>? = null
)
