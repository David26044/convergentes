package co.edu.unipiloto.proyectodconvergentes.ui.net

import co.edu.unipiloto.proyectodconvergentes.ui.driver.DriverRegisterRequest
import co.edu.unipiloto.proyectodconvergentes.ui.driver.DriverResponse
import co.edu.unipiloto.proyectodconvergentes.ui.location.LocationResponse
import co.edu.unipiloto.proyectodconvergentes.ui.locationType.LocationTypeResponse
import co.edu.unipiloto.proyectodconvergentes.ui.login.UserLoginRequest
import co.edu.unipiloto.proyectodconvergentes.ui.login.UserLoginResponse
import co.edu.unipiloto.proyectodconvergentes.ui.order.AssignDriver
import co.edu.unipiloto.proyectodconvergentes.ui.order.OrderRequest
import co.edu.unipiloto.proyectodconvergentes.ui.order.OrderResponse
import co.edu.unipiloto.proyectodconvergentes.ui.order.PaymentMethodResponse
import co.edu.unipiloto.proyectodconvergentes.ui.order.ProductTypeResponse
import co.edu.unipiloto.proyectodconvergentes.ui.qualification.QualificationResponse
import co.edu.unipiloto.proyectodconvergentes.ui.qualification.RegisterQualificationRequest
import co.edu.unipiloto.proyectodconvergentes.ui.register.UserRegisterRequest
import co.edu.unipiloto.proyectodconvergentes.ui.register.UserRegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface BackendService {
    @POST(Constants.PATH_REGISTER)
    suspend fun registerNewUser(@Body userRequest: UserRegisterRequest): Response<UserRegisterResponse>

    @POST(Constants.PATH_LOGIN)
    suspend fun loginUser(@Body loginRequest: UserLoginRequest): Response<UserLoginResponse>

    @POST(Constants.PATH_REGISTER_ORDER)
    suspend fun  registerOrder(@Body orderRequest: OrderRequest): Response<OrderResponse>

    @GET(Constants.PATH_GET_PAYMENT_METHODS)
    suspend fun getPaymentMethods(): Response<List<PaymentMethodResponse>>

    @GET(Constants.PATH_GET_PRODUCT_TYPES)
    suspend fun getProductTypes(): Response<List<ProductTypeResponse>>

    @GET(Constants.PATH_GET_LOCATYON_TYPES)
    suspend fun getLocationTypes(): Response<List<LocationTypeResponse>>

    @GET(Constants.PATH_GET_MY_ORDERS)
    suspend fun getMyOrders(): Response<List<OrderResponse>>

    @GET(Constants.PATH_GET_ALL_ORDERS)
    suspend fun getAllOrders(): Response<List<OrderResponse>>
    @GET(Constants.PATH_GET_ALL_DRIVERS)
    suspend fun getAllDrivers(): Response<List<DriverResponse>>
    @GET(Constants.PATH_GET_ORDERS_WITHOUT_DRIVER)
    suspend fun  getOrdersWithOutDriver(): Response<List<OrderResponse>>
    @GET(Constants.PATH_GET_ORDERS_ME_DRIVER)
    suspend fun getOrdersByDriver(): Response<List<OrderResponse>>

    @PATCH("locations/{locationId}/assign-drivers")
    suspend fun assignDriver(
        @Path("locationId") locationId: Long,
        @Body assignDriverRequest: AssignDriver,

    ): Response<LocationResponse>

    @PATCH("orders/{orderId}/next-state")
    suspend fun orderNextState(
        @Path("orderId") orderId: Long
    ): Response<OrderResponse>

    @POST(Constants.PATH_POST_DRIVER)
    suspend fun registerDriver(@Body driverRequest: DriverRegisterRequest): Response<DriverResponse>

    @POST(Constants.PATH_CREATE_QUALIFICATION) suspend fun createQualification(
        @Body request: RegisterQualificationRequest): Response<QualificationResponse>

    @GET(Constants.PATH_GET_ORDERS_ME_DELIVERED) suspend fun getOrdersDelivered(): Response<List<OrderResponse>>

    @GET("qualifications")
    suspend fun getAllQualifications(): Response<List<QualificationResponse>>
}
