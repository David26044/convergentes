package co.edu.unipiloto.proyectodconvergentes.ui.odometer

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import kotlinx.coroutines.*

class OdometerService : Service() {

    // Binder para que las Activities obtengan una referencia al servicio
    private val binder = OdometerBinder()

    // Simulación de odómetro
    private var isRunning = false
    private var distanceMeters: Double = 0.0
    private var currentOrderId: Long? = null

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    inner class OdometerBinder : Binder() {
        fun getService(): OdometerService = this@OdometerService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    // Arranca el conteo para una orden
    fun startOdometer(orderId: Long) {
        if (isRunning) return

        currentOrderId = orderId
        isRunning = true
        job = scope.launch {
            while (isRunning) {
                delay(1000L)              // cada segundo
                distanceMeters += 50.0    // sumamos 50 metros simulados
            }
        }
    }

    // Detiene el conteo
    fun stopOdometer() {
        isRunning = false
        job?.cancel()
        job = null
    }

    fun reset() {
        distanceMeters = 0.0
        currentOrderId = null
    }

    // Devuelve la distancia en kilómetros
    fun getDistanceKm(): Double {
        return distanceMeters / 1000.0
    }

    fun getCurrentOrderId(): Long? = currentOrderId
}
