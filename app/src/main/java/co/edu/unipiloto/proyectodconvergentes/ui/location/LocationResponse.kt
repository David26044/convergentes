package co.edu.unipiloto.proyectodconvergentes.ui.location

import android.R
import co.edu.unipiloto.proyectodconvergentes.ui.driver.DriverResponse
import co.edu.unipiloto.proyectodconvergentes.ui.locationType.LocationTypeResponse
import java.time.LocalDate
import java.time.LocalTime

data class LocationResponse(val id: Long,
                            val city: String,
                            val typeVia: String,
                            val numberVia: String,
                            val apartment: String?,
                            val block: String?,
                            val instructions: String?,
                            val askFor: String?,
                            val date: String?,
                            val hour: String?,
                            val locationType: LocationTypeResponse,
                            val driver: DriverResponse?
    )


/*
* Long id,
        String city,
        String typeVia,
        String numberVia,
        String apartment,
        String block,
        String instructions,
        String askFor,
        LocalDate date,
        LocalTime hour,
        LocationTypeResponse locationType,
        DriverResponse driver
* */