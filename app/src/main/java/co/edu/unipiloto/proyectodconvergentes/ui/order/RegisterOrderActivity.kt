package co.edu.unipiloto.proyectodconvergentes.ui.order

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import co.edu.unipiloto.proyectodconvergentes.R
import co.edu.unipiloto.proyectodconvergentes.ui.location.LocationRequest
import co.edu.unipiloto.proyectodconvergentes.ui.net.RetrofitModule
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class RegisterOrderActivity : AppCompatActivity() {

    private lateinit var spProductType: Spinner
    private lateinit var etDeclaredValue: EditText
    private lateinit var spPaymentMethod: Spinner
    private lateinit var btnRegisterDelivery: Button

    private lateinit var etCityPickUp: EditText
    private lateinit var etTypeViaPickUp: EditText
    private lateinit var etNumberViaPickUp: EditText
    private lateinit var etApartmentPickUp: EditText
    private lateinit var etBlockPickUp: EditText
    private lateinit var etInstructionsPickUp: EditText
    private lateinit var etAskForPickUp: EditText
    private lateinit var etDatePickUp: EditText
    private lateinit var etHourPickUp: EditText

    private lateinit var etCityDelivery: EditText
    private lateinit var etTypeViaDelivery: EditText
    private lateinit var etNumberViaDelivery: EditText
    private lateinit var etApartmentDelivery: EditText
    private lateinit var etBlockDelivery: EditText
    private lateinit var etInstructionsDelivery: EditText
    private lateinit var etAskForDelivery: EditText

    private var paymentMethods: List<PaymentMethodResponse> = emptyList()
    private var productTypes: List<ProductTypeResponse> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_order)

        // Inicializar vistas
        spProductType = findViewById(R.id.spProductType)
        etDeclaredValue = findViewById(R.id.etDeclaredValue)
        spPaymentMethod = findViewById(R.id.spPaymentMethod)
        btnRegisterDelivery = findViewById(R.id.btnRegisterDelivery)

        etCityPickUp = findViewById(R.id.etCityPickUp)
        etTypeViaPickUp = findViewById(R.id.etTypeViaPickUp)
        etNumberViaPickUp = findViewById(R.id.etNumberViaPickUp)
        etApartmentPickUp = findViewById(R.id.etApartmentPickUp)
        etBlockPickUp = findViewById(R.id.etBlockPickUp)
        etInstructionsPickUp = findViewById(R.id.etInstructionsPickUp)
        etAskForPickUp = findViewById(R.id.etAskForPickUp)
        etDatePickUp = findViewById(R.id.etDatePickUp)
        etHourPickUp = findViewById(R.id.etHourPickUp)

        etCityDelivery = findViewById(R.id.etCityDelivery)
        etTypeViaDelivery = findViewById(R.id.etTypeViaDelivery)
        etNumberViaDelivery = findViewById(R.id.etNumberViaDelivery)
        etApartmentDelivery = findViewById(R.id.etApartmentDelivery)
        etBlockDelivery = findViewById(R.id.etBlockDelivery)
        etInstructionsDelivery = findViewById(R.id.etInstructionsDelivery)
        etAskForDelivery = findViewById(R.id.etAskForDelivery)

        // Cargar listas
        loadProductTypes()
        loadPaymentMethods()

        // Selectores de fecha y hora (solo para pickup)
        etDatePickUp.setOnClickListener { showDatePicker(etDatePickUp) }
        etHourPickUp.setOnClickListener { showTimePicker(etHourPickUp) }

        // Botón registrar
        btnRegisterDelivery.setOnClickListener {
            if (validateFields()) {
                sendOrder()
            }
        }
    }

    private fun loadProductTypes() {
        val service = RetrofitModule.backendService(this)
        lifecycleScope.launch {
            try {
                val resp = service.getProductTypes()
                if (resp.isSuccessful) {
                    val products = resp.body().orEmpty()
                    productTypes = products

                    val names = mutableListOf("Seleccione tipo de producto")
                    names.addAll(products.map { it.name })

                    val adapter = ArrayAdapter(
                        this@RegisterOrderActivity,
                        android.R.layout.simple_spinner_item,
                        names
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spProductType.adapter = adapter
                } else {
                    Toast.makeText(
                        this@RegisterOrderActivity,
                        "Error cargando productos ${resp.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@RegisterOrderActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadPaymentMethods() {
        val service = RetrofitModule.backendService(this)
        lifecycleScope.launch {
            try {
                val resp = service.getPaymentMethods()
                if (resp.isSuccessful) {
                    val methods = resp.body().orEmpty()
                    paymentMethods = methods

                    val names = mutableListOf("Seleccione método de pago")
                    names.addAll(methods.map { it.name })

                    val adapter = ArrayAdapter(
                        this@RegisterOrderActivity,
                        android.R.layout.simple_spinner_item,
                        names
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spPaymentMethod.adapter = adapter
                } else {
                    Toast.makeText(
                        this@RegisterOrderActivity,
                        "Error cargando métodos de pago ${resp.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@RegisterOrderActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePicker(target: EditText) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                calendar.set(year, month, day)
                target.setText(sdf.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker(target: EditText) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            this,
            { _, hour, minute ->
                target.setText(String.format("%02d:%02d:00", hour, minute))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun sendOrder() {
        val posProduct = spProductType.selectedItemPosition
        val posPayment = spPaymentMethod.selectedItemPosition

        val selectedProduct = productTypes[posProduct - 1]
        val selectedMethod = paymentMethods[posPayment - 1]

        val pickupLocation = LocationRequest(
            city = etCityPickUp.text.toString(),
            typeVia = etTypeViaPickUp.text.toString(),
            numberVia = etNumberViaPickUp.text.toString(),
            apartment = etApartmentPickUp.text.toString(),
            block = etBlockPickUp.text.toString(),
            instructions = etInstructionsPickUp.text.toString(),
            askFor = etAskForPickUp.text.toString(),
            date = etDatePickUp.text.toString(),
            hour = etHourPickUp.text.toString(),
            driverId = null,
            typeLocationId = 1
        )

        val deliveryLocation = LocationRequest(
            city = etCityDelivery.text.toString(),
            typeVia = etTypeViaDelivery.text.toString(),
            numberVia = etNumberViaDelivery.text.toString(),
            apartment = etApartmentDelivery.text.toString(),
            block = etBlockDelivery.text.toString(),
            instructions = etInstructionsDelivery.text.toString(),
            askFor = etAskForDelivery.text.toString(),
            date = null,   // ❌ no se manda en delivery
            hour = null,   // ❌ no se manda en delivery
            driverId = null,
            typeLocationId = 2
        )

        val orderRequest = OrderRequest(
            productTypeId = selectedProduct.id,
            paymentMethodId = selectedMethod.id,
            declaredValue = etDeclaredValue.text.toString().toDouble(),
            pickupLocation = pickupLocation,
            deliveryLocation = deliveryLocation
        )

        val service = RetrofitModule.backendService(this)

        lifecycleScope.launch {
            try {
                val resp = service.registerOrder(orderRequest)
                if (resp.isSuccessful) {
                    val orderResponse = resp.body()
                    if (orderResponse != null) {
                        Toast.makeText(
                            this@RegisterOrderActivity,
                            "✅ Orden creada con ID: ${orderResponse.id}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@RegisterOrderActivity,
                        "❌ Error al crear orden: ${resp.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@RegisterOrderActivity, "Excepción: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun validateFields(): Boolean {
        if (spProductType.selectedItemPosition <= 0) {
            Toast.makeText(this, "Selecciona un tipo de producto", Toast.LENGTH_SHORT).show()
            return false
        }
        if (etDeclaredValue.text.isNullOrBlank()) {
            etDeclaredValue.error = "Obligatorio"
            etDeclaredValue.requestFocus()
            return false
        }
        if (spPaymentMethod.selectedItemPosition <= 0) {
            Toast.makeText(this, "Selecciona un método de pago", Toast.LENGTH_SHORT).show()
            return false
        }
        if (etCityPickUp.text.isNullOrBlank() || etTypeViaPickUp.text.isNullOrBlank() ||
            etNumberViaPickUp.text.isNullOrBlank() || etDatePickUp.text.isNullOrBlank() ||
            etHourPickUp.text.isNullOrBlank() || etAskForPickUp.text.isNullOrBlank()
        ) {
            Toast.makeText(this, "Completa todos los campos de recogida", Toast.LENGTH_SHORT).show()
            return false
        }
        if (etCityDelivery.text.isNullOrBlank() || etTypeViaDelivery.text.isNullOrBlank() ||
            etNumberViaDelivery.text.isNullOrBlank() || etAskForDelivery.text.isNullOrBlank()
        ) {
            Toast.makeText(this, "Completa todos los campos de entrega", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
