package co.edu.unipiloto.proyectodconvergentes.ui.incident

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.edu.unipiloto.proyectodconvergentes.R

class RegisterIncidentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_incident)

        val etDescriptionIncident = findViewById<EditText>(R.id.etDescriptionIncident)
        val etDelayTime = findViewById<EditText>(R.id.etDelayTime)
        val btnRegisterIncident = findViewById<Button>(R.id.btnRegisterIncident)

        val descriptionIncident = etDescriptionIncident.text.toString()
        val delayTime = etDelayTime.text.toString()

        btnRegisterIncident.setOnClickListener {

            if (descriptionIncident.isEmpty() || delayTime.isEmpty()){
                    Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }


        }

        }
    }