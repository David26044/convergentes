package co.edu.unipiloto.proyectodconvergentes.ui.order

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import co.edu.unipiloto.proyectodconvergentes.R
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.Calendar

class RegisterOrderActivity : AppCompatActivity() {

    private lateinit var etProductType: EditText
    private lateinit var etDeclaredValue: EditText
    private lateinit var spPaymentMethod: Spinner
    private lateinit var btnRegisterDelivery: Button

    private lateinit var etCityPickUp: EditText
    private lateinit var etTypeViaPickUp: EditText
    private lateinit var etDatePickUp: EditText
    private lateinit var etHourPickUp: EditText
    private lateinit var etAskForPickUp: EditText

    private lateinit var etCityDelivery: EditText
    private lateinit var etTypeViaDelivery: EditText
    private lateinit var etNumberViaDelivery: EditText
    private lateinit var etAskForDelivery: EditText

    private var paymentMethods: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_order)

        etProductType = findViewById(R.id.etProductType)
        etDeclaredValue = findViewById(R.id.etDeclaredValue)
        spPaymentMethod = findViewById(R.id.spPaymentMethod)
        btnRegisterDelivery = findViewById(R.id.btnRegisterDelivery)

        etCityPickUp = findViewById(R.id.etCityPickUp)
        etTypeViaPickUp = findViewById(R.id.etTypeViaPickUp)
        etDatePickUp = findViewById(R.id.etDatePickUp)
        etHourPickUp = findViewById(R.id.etHourPickUp)
        etAskForPickUp = findViewById(R.id.etAskForPickUp)

        etCityDelivery = findViewById(R.id.etCityDelivery)
        etTypeViaDelivery = findViewById(R.id.etTypeViaDelivery)
        etNumberViaDelivery = findViewById(R.id.etNumberViaDelivery)
        etAskForDelivery = findViewById(R.id.etAskForDelivery)

        loadPaymentMethods()

        etDatePickUp.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this,
                { _, year, month, day ->
                    etDatePickUp.setText("$day/${month+1}/$year")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        etHourPickUp.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(this,
                { _, hour, minute ->
                    etHourPickUp.setText(String.format("%02d:%02d", hour, minute))
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }


        btnRegisterDelivery.setOnClickListener {
            if (validateFields()) {
                Toast.makeText(this, "Datos válidos. Registrando pedido...", Toast.LENGTH_SHORT).show()

                //ACÁ LLAMO A BACKEND PARA REGISTRAR PEDIDO
            }
        }
    }

    private fun loadPaymentMethods() {
        //Consumo de API para llenar el spinner
        val paymentMethods = listOf("Seleccione método de pago", "Efectivo", "Tarjeta", "Transferencia")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            paymentMethods
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spPaymentMethod.adapter = adapter
    }

    private fun validateFields(): Boolean {
        // Producto
        if (etProductType.text.isNullOrBlank()) {
            etProductType.error = "Obligatorio"
            etProductType.requestFocus()
            return false
        }

        if (etDeclaredValue.text.isNullOrBlank()) {
            etDeclaredValue.error = "Obligatorio"
            etDeclaredValue.requestFocus()
            return false
        }

        if (spPaymentMethod.selectedItem == null || spPaymentMethod.selectedItem.toString().isBlank()) {
            Toast.makeText(this, "Selecciona un método de pago", Toast.LENGTH_SHORT).show()
            spPaymentMethod.requestFocus()
            return false
        }

        // Recogida
        if (etCityPickUp.text.isNullOrBlank()) {
            etCityPickUp.error = "Obligatorio"
            etCityPickUp.requestFocus()
            return false
        }
        if (etTypeViaPickUp.text.isNullOrBlank()) {
            etTypeViaPickUp.error = "Obligatorio"
            etTypeViaPickUp.requestFocus()
            return false
        }
        if (etDatePickUp.text.isNullOrBlank()) {
            etDatePickUp.error = "Obligatorio"
            etDatePickUp.requestFocus()
            return false
        }
        if (etHourPickUp.text.isNullOrBlank()) {
            etHourPickUp.error = "Obligatorio"
            etHourPickUp.requestFocus()
            return false
        }
        if (etAskForPickUp.text.isNullOrBlank()) {
            etAskForPickUp.error = "Obligatorio"
            etAskForPickUp.requestFocus()
            return false
        }

        // Entrega
        if (etCityDelivery.text.isNullOrBlank()) {
            etCityDelivery.error = "Obligatorio"
            etCityDelivery.requestFocus()
            return false
        }
        if (etTypeViaDelivery.text.isNullOrBlank()) {
            etTypeViaDelivery.error = "Obligatorio"
            etTypeViaDelivery.requestFocus()
            return false
        }
        if (etNumberViaDelivery.text.isNullOrBlank()) {
            etNumberViaDelivery.error = "Obligatorio"
            etNumberViaDelivery.requestFocus()
            return false
        }
        if (etAskForDelivery.text.isNullOrBlank()) {
            etAskForDelivery.error = "Obligatorio"
            etAskForDelivery.requestFocus()
            return false
        }

        return true
    }
}
