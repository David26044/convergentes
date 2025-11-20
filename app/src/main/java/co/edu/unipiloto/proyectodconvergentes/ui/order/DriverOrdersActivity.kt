package co.edu.unipiloto.proyectodconvergentes.ui.order

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.edu.unipiloto.proyectodconvergentes.R
import co.edu.unipiloto.proyectodconvergentes.ui.maps.MapsNavigator
import co.edu.unipiloto.proyectodconvergentes.ui.net.JwtDecoder
import co.edu.unipiloto.proyectodconvergentes.ui.net.RetrofitModule
import co.edu.unipiloto.proyectodconvergentes.ui.net.TokenManager
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope

class DriverOrdersActivity : AppCompatActivity() {

    // Listas gráficas
    private lateinit var recyclerPickup: RecyclerView
    private lateinit var recyclerDelivery: RecyclerView

    // Adaptadores para cada lista
    private lateinit var adapterPickup: DriverOrdersAdapter
    private lateinit var adapterDelivery: DriverOrdersAdapter

    // Botones para trazar rutas con varias paradas
    private lateinit var btnRutaPickup: Button
    private lateinit var btnRutaDelivery: Button

    // Maneja el token JWT del usuario logueado
    private val tokenManager by lazy { TokenManager(this) }

    // Direcciones en texto para las recogidas y entregas
    private var pickupAddresses: List<String> = emptyList()
    private var deliveryAddresses: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_orders)

        recyclerPickup = findViewById(R.id.recyclerPickupOrders)
        recyclerDelivery = findViewById(R.id.recyclerDeliveryOrders)
        btnRutaPickup = findViewById(R.id.btnRutaPickup)
        btnRutaDelivery = findViewById(R.id.btnRutaDelivery)

        recyclerPickup.layoutManager = LinearLayoutManager(this)
        recyclerDelivery.layoutManager = LinearLayoutManager(this)

        //Crear adaptador para órdenes de recogida
        adapterPickup = DriverOrdersAdapter(
            onNavigateClick = { order ->
                // Construimos la dirección de recogida de ESA orden
                val address = MapsNavigator.buildAddress(
                    order.pickUpLocation.city,
                    order.pickUpLocation.typeVia,
                    order.pickUpLocation.numberVia
                )
                // Abrimos Google Maps solo con esa parada
                val singleStopList = listOf(address)
                MapsNavigator.openMultiStopFromCurrentLocation(this, singleStopList)
            },
            buttonLabel = "Ir a Recogida"
        )

        // 4. Crear adaptador para órdenes de entrega
        adapterDelivery = DriverOrdersAdapter(
            onNavigateClick = { order ->
                // Construimos la dirección de entrega de ESA orden
                val address = MapsNavigator.buildAddress(
                    order.deliveryLocation.city,
                    order.deliveryLocation.typeVia,
                    order.deliveryLocation.numberVia
                )
                val singleStopList = listOf(address)
                MapsNavigator.openMultiStopFromCurrentLocation(this, singleStopList)
            },
            buttonLabel = "Ir a Entrega"
        )

        recyclerPickup.adapter = adapterPickup
        recyclerDelivery.adapter = adapterDelivery

        // 6. Botones para rutas múltiples (todas las recogidas / todas las entregas)
        btnRutaPickup.setOnClickListener {
            openMultiStopPickup()
        }

        btnRutaDelivery.setOnClickListener {
            openMultiStopDelivery()
        }

        //Pedir permiso de ubicación y luego cargar las órdenes
        requestLocationPermissionAndLoad()
    }

    /**
     * Verifica si ya tenemos permiso de ubicación.
     * Si no lo tenemos, lo pide.
     * Si sí lo tenemos, carga las órdenes desde el backend.
     */
    private fun requestLocationPermissionAndLoad() {
        val permissionStatus = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
            // No hay permiso, se solicita al usuario
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
        } else {
            // Ya tenemos permiso, podemos cargar las órdenes
            loadOrders()
        }
    }

    /**
     * Abre una ruta con TODAS las direcciones de recogida.
     */
    private fun openMultiStopPickup() {
        if (pickupAddresses.isEmpty()) {
            Toast.makeText(this, "No hay recogidas asignadas", Toast.LENGTH_SHORT).show()
            return
        }
        // Pasamos la lista completa de paradas a MapsNavigator
        MapsNavigator.openMultiStopFromCurrentLocation(this, pickupAddresses)
    }

    /**
     * Abre una ruta con TODAS las direcciones de entrega.
     */
    private fun openMultiStopDelivery() {
        if (deliveryAddresses.isEmpty()) {
            Toast.makeText(this, "No hay entregas asignadas", Toast.LENGTH_SHORT).show()
            return
        }
        MapsNavigator.openMultiStopFromCurrentLocation(this, deliveryAddresses)
    }

    /**
     * Carga las órdenes desde el backend, filtra cuáles son del conductor,
     * separa en recogidas y entregas, y arma las listas de direcciones.
     */
    private fun loadOrders() {
        // Corrutina ligada al ciclo de vida de la Activity
        lifecycleScope.launch {
            try {
                // Cliente Retrofit para hablar con el backend
                val service = RetrofitModule.backendService(this@DriverOrdersActivity)

                // Llamado a la API: obtener órdenes del conductor
                val response = service.getOrdersByDriver()

                if (response.isSuccessful) {
                    // Si la respuesta fue exitosa, recibimos la lista de órdenes
                    val orders = response.body() ?: emptyList()

                    // Obtenemos el token JWT guardado
                    val token = tokenManager.getToken()

                    // Sacamos los "claims" (datos del JWT)
                    val claims = if (token != null) JwtDecoder.extractClaims(token) else null

                    // Normalmente el "subject" del token es el correo del usuario
                    val email = claims?.subject

                    // Lista para órdenes de recogida
                    val pickupOrders = mutableListOf<OrderResponse>()
                    // Lista para órdenes de entrega
                    val deliveryOrders = mutableListOf<OrderResponse>()

                    // Recorremos TODAS las órdenes y separamos según el caso
                    for (order in orders) {

                        // Verificar si esta orden es de recogida para este conductor
                        val pickupDriverEmail = order.pickUpLocation.driver?.email
                        if (pickupDriverEmail == email && order.orderState.state == "CREADA") {
                            pickupOrders.add(order)
                        }

                        // Verificar si esta orden es de entrega para este conductor
                        val deliveryDriverEmail = order.deliveryLocation.driver?.email
                        if (deliveryDriverEmail == email && order.orderState.state == "EN_CAMINO") {
                            deliveryOrders.add(order)
                        }
                    }

                    // Pasamos las listas filtradas a los adaptadores
                    adapterPickup.submitList(pickupOrders)
                    adapterDelivery.submitList(deliveryOrders)

                    // Construimos las direcciones de recogida en texto
                    val pickupAddressesList = mutableListOf<String>()
                    for (order in pickupOrders) {
                        val address = MapsNavigator.buildAddress(
                            order.pickUpLocation.city,
                            order.pickUpLocation.typeVia,
                            order.pickUpLocation.numberVia
                        )
                        pickupAddressesList.add(address)
                    }
                    pickupAddresses = pickupAddressesList

                    // Construimos las direcciones de entrega en texto
                    val deliveryAddressesList = mutableListOf<String>()
                    for (order in deliveryOrders) {
                        val address = MapsNavigator.buildAddress(
                            order.deliveryLocation.city,
                            order.deliveryLocation.typeVia,
                            order.deliveryLocation.numberVia
                        )
                        deliveryAddressesList.add(address)
                    }
                    deliveryAddresses = deliveryAddressesList

                } else {
                    // Respuesta HTTP no fue exitosa (404, 500, etc.)
                    Toast.makeText(
                        this@DriverOrdersActivity,
                        "Error cargando órdenes",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                // Error de red, timeout, etc.
                Toast.makeText(
                    this@DriverOrdersActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
