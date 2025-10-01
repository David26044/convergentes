package co.edu.unipiloto.proyectodconvergentes.ui.order

import co.edu.unipiloto.proyectodconvergentes.ui.location.LocationRequest

data class OrderRequest(
    val productTypeId: Long,
    val paymentMethodId: Long,
    val declaredValue: Double,
    val pickupLocation: LocationRequest,
    val deliveryLocation: LocationRequest
)