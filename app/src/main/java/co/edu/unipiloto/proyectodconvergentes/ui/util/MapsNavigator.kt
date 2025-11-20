package co.edu.unipiloto.proyectodconvergentes.ui.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Objeto encargado de abrir rutas en Google Maps a partir de direcciones o coordenadas.
 */
object MapsNavigator {

    /**
     * Función auxiliar para codificar texto en URL (espacios, tildes, etc.).
     */
    private fun enc(s: String): String {
        return URLEncoder.encode(s, StandardCharsets.UTF_8.toString())
    }

    /**
     * Construye una dirección de texto a partir de ciudad, tipo de vía y número.
     * Ejemplo: "Calle 80 #90-10, Bogotá, Colombia"
     */
    fun buildAddress(city: String, typeVia: String, numberVia: String): String {
        return "$typeVia $numberVia, $city, Colombia"
    }

    /**
     * Obtiene la ubicación actual del dispositivo y luego abre la ruta en Google Maps.
     * Si no se tiene permiso de ubicación, solo muestra un Toast.
     *
     * @param context contexto de la Activity / aplicación
     * @param stops lista de direcciones en texto (paradas)
     * @param travelMode modo de viaje: "driving", "walking", etc.
     */
    @SuppressLint("MissingPermission")
    fun openMultiStopFromCurrentLocation(
        context: Context,
        stops: List<String>,
        travelMode: String = "driving"
    ) {
        // Cliente de localización de Google (GPS + WiFi + red, etc.)
        val fusedLocation: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

        // Verificar permiso de ubicación fina
        val permissionStatus = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Permiso de ubicación no concedido", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener la última ubicación conocida
        fusedLocation.lastLocation.addOnSuccessListener { location: Location? ->
            if (location == null) {
                Toast.makeText(
                    context,
                    "No se pudo obtener ubicación actual",
                    Toast.LENGTH_SHORT
                ).show()
                return@addOnSuccessListener
            }

            val lat = location.latitude
            val lon = location.longitude
            val origin = "$lat,$lon"

            // Una vez tenemos el origen, abrimos la ruta con las paradas
            openRouteWithOrigin(context, stops, origin, travelMode)
        }
    }

    /**
     * Crea la URL final para Google Maps y la abre con un Intent.
     *
     * @param context contexto
     * @param rawStops lista original de paradas (pueden tener espacios o repetidos)
     * @param origin origen en formato "lat,lon"
     * @param travelMode modo de viaje
     */
    private fun openRouteWithOrigin(
        context: Context,
        rawStops: List<String>,
        origin: String,
        travelMode: String
    ) {
        try {
            // 1. Limpiamos la lista de paradas:
            // - Quitamos espacios extra
            // - Eliminamos vacíos
            // - Evitamos duplicados
            val stops = mutableListOf<String>()
            for (raw in rawStops) {
                val trimmed = raw.trim()
                if (trimmed.isNotEmpty() && !stops.contains(trimmed)) {
                    stops.add(trimmed)
                }
            }

            if (stops.isEmpty()) {
                Toast.makeText(context, "No hay paradas", Toast.LENGTH_SHORT).show()
                return
            }

            // 2. Si solo hay UNA parada: origen -> destino (simple)
            if (stops.size == 1) {
                val destinationEncoded = enc(stops[0])
                val originEncoded = enc(origin)
                val modeEncoded = enc(travelMode)

                val url = "https://www.google.com/maps/dir/?api=1" +
                        "&origin=$originEncoded" +
                        "&destination=$destinationEncoded" +
                        "&travelmode=$modeEncoded"

                val uri = Uri.parse(url)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.setPackage("com.google.android.apps.maps") // forzar Google Maps
                context.startActivity(intent)
                return
            }

            // 3. Si hay varias paradas: usamos waypoints + destino final
            val originEncoded = enc(origin)
            val modeEncoded = enc(travelMode)

            // El destino será la ÚLTIMA parada de la lista
            val lastIndex = stops.size - 1
            val destinationEncoded = enc(stops[lastIndex])

            // Waypoints = todas las paradas menos la última, con formato "via:direccion"
            val waypointsBuilder = StringBuilder()
            for (i in 0 until lastIndex) {
                val stop = stops[i]
                val stopEncoded = enc(stop)
                if (i > 0) {
                    waypointsBuilder.append("|")
                }
                waypointsBuilder.append("via:")
                waypointsBuilder.append(stopEncoded)
            }
            val waypointsParam = waypointsBuilder.toString()

            val url = "https://www.google.com/maps/dir/?api=1" +
                    "&origin=$originEncoded" +
                    "&destination=$destinationEncoded" +
                    "&waypoints=$waypointsParam" +
                    "&travelmode=$modeEncoded"

            val uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.google.android.apps.maps")
            context.startActivity(intent)

        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
