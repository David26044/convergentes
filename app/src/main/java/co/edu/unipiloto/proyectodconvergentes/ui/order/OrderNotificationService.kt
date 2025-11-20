package co.edu.unipiloto.proyectodconvergentes.ui.order

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import co.edu.unipiloto.proyectodconvergentes.R

class OrderNotificationService : IntentService("OrderNotificationService") {

    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_MESSAGE = "extra_message"
        const val EXTRA_ORDER_ID = "extra_order_id"

        private const val CHANNEL_ID = "orders_channel"
    }

    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) return

        val title = intent.getStringExtra(EXTRA_TITLE) ?: "Encomienda"
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "Actualización de encomienda"
        val orderId = intent.getStringExtra(EXTRA_ORDER_ID) ?: "0"

        createNotificationChannel()

        // Cuando el usuario toca la notificación, vuelve a esta pantalla
        val activityIntent = Intent(this, DriverOrdersByStateActivity::class.java)

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            activityIntent,
            flags
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo_empresa)   // ícono de tu app
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationId = orderId.hashCode()

        // En Android 13+ solo se muestra si el permiso fue aceptado
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED
            if (!granted) {
                return
            }
        }

        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, notification)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notificaciones de encomiendas"
            val descriptionText = "Avisos de llegada y entrega de encomiendas"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val manager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }
}
