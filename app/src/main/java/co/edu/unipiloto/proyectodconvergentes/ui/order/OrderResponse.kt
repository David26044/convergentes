package co.edu.unipiloto.proyectodconvergentes.ui.order

import co.edu.unipiloto.proyectodconvergentes.ui.location.LocationResponse
import co.edu.unipiloto.proyectodconvergentes.ui.orderState.OrderStateResponse
import co.edu.unipiloto.proyectodconvergentes.ui.user.UserResponse

data class OrderResponse(
    val id: Long,
    val productType: ProductTypeResponse,
    val paymentMethod: PaymentMethodResponse,
    val declaredValue: Double,
    val remitent: UserResponse,
    val pickUpLocation: LocationResponse,
    val deliveryLocation: LocationResponse,
    val orderState: OrderStateResponse
)
