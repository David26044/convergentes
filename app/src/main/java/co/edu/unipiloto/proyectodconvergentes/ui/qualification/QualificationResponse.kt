package co.edu.unipiloto.proyectodconvergentes.ui.qualification

import co.edu.unipiloto.proyectodconvergentes.ui.order.OrderResponse

data class QualificationResponse(
    val id: Long,
    val score: Int,
    val comment: String?,
    val order: OrderResponse
)