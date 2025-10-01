package co.edu.unipiloto.proyectodconvergentes.ui.location

data class LocationRequest(
    val city: String,
    val typeVia: String,
    val numberVia: String,
    val apartment: String?,
    val block: String?,
    val instructions: String,
    val askFor: String,
    val date: String?,       // formato: "yyyy-MM-dd"
    val hour: String?,       // formato: "HH:mm:ss"
    val driverId: Long?,
    val typeLocationId: Long
)