package co.edu.unipiloto.proyectodconvergentes.ui.net

object Constants {

    const val BASE_URL = "http://10.0.2.2:8080/system/api/"
    const val PATH_REGISTER = "auth/register"
    const val PATH_LOGIN = "auth/login"
    const val PATH_REGISTER_ORDER = "orders"
    const val PATH_GET_PAYMENT_METHODS = "payment-methods"
    const val PATH_GET_PRODUCT_TYPES = "product-types"
    const val PATH_GET_LOCATYON_TYPES = "location-types"
    const val PATH_GET_ALL_ORDERS = "orders"
    const val PATH_GET_MY_ORDERS = "orders/me"
    const val PATH_GET_ORDERS_WITHOUT_DRIVER = "orders/unassigned"
    const val PATH_ASSIGN_DRIVER = "orders/{orderId}/assign-drivers"
    const val PATH_GET_ALL_DRIVERS = "drivers"

    const val PATH_GET_ORDERS_ME_DRIVER = "orders/me/driver"
}