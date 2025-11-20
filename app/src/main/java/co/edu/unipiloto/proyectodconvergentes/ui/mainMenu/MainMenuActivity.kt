package co.edu.unipiloto.proyectodconvergentes.ui.mainMenu

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import co.edu.unipiloto.proyectodconvergentes.R
import co.edu.unipiloto.proyectodconvergentes.ui.driver.RegisterDriverActivity
import co.edu.unipiloto.proyectodconvergentes.ui.driver.ViewDriversActivity
import co.edu.unipiloto.proyectodconvergentes.ui.incident.RegisterIncidentActivity
import co.edu.unipiloto.proyectodconvergentes.ui.login.LoginActivity
import co.edu.unipiloto.proyectodconvergentes.ui.net.JwtDecoder
import co.edu.unipiloto.proyectodconvergentes.ui.net.Roles
import co.edu.unipiloto.proyectodconvergentes.ui.net.TokenManager
import co.edu.unipiloto.proyectodconvergentes.ui.order.AssignDeliveryDriverActivity
import co.edu.unipiloto.proyectodconvergentes.ui.order.AssignPickupDriverActivity
import co.edu.unipiloto.proyectodconvergentes.ui.order.DriverOrdersActivity
import co.edu.unipiloto.proyectodconvergentes.ui.order.DriverOrdersByStateActivity
import co.edu.unipiloto.proyectodconvergentes.ui.order.GetMyOrdersActivity
import co.edu.unipiloto.proyectodconvergentes.ui.order.RegisterOrderActivity
import co.edu.unipiloto.proyectodconvergentes.ui.qualification.AllQualificationsActivity
import co.edu.unipiloto.proyectodconvergentes.ui.qualification.GetDeliveredOrdersForQualificationActivity
import co.edu.unipiloto.proyectodconvergentes.ui.user.UserProfileActivity
import kotlinx.coroutines.launch

class MainMenuActivity : AppCompatActivity() {

    private val tokenManager by lazy { TokenManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        // Logo ahora en el menú, no en el toolbar
        val imgLogo = findViewById<ImageView>(R.id.imgLogo)
        imgLogo.setOnClickListener {
            Toast.makeText(this, "Logística ALMAROS", Toast.LENGTH_SHORT).show()
        }

        // Botones del toolbar
        val btnPerfil = findViewById<Button>(R.id.btnPerfil)
        val btnLogoutToolbar = findViewById<Button>(R.id.btnLogoutToolbar)

        btnPerfil.setOnClickListener {
            startActivity(Intent(this, UserProfileActivity::class.java))
        }

        btnLogoutToolbar.setOnClickListener {
            lifecycleScope.launch {
                tokenManager.clear()
                goToLogin()
            }
        }

        // Botones del menú
        val btnCreateOrder = findViewById<Button>(R.id.btnCreateOrder)
        val btnViewOrders = findViewById<Button>(R.id.btnViewOrders)
        val btnAssignOrders = findViewById<Button>(R.id.btnAssignOrders)
        val btnAssignInHub = findViewById<Button>(R.id.btnAssignInHub)
        val btnMyOrders = findViewById<Button>(R.id.btnMyOrders)
        val btnRegisterIncident = findViewById<Button>(R.id.btnRegisterIncident)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val btnDriverOrders = findViewById<Button>(R.id.btnDriverOrders)
        val btnChangeOrderState = findViewById<Button>(R.id.btnChangeOrderState)
        val btnRegisterDriver = findViewById<Button>(R.id.btnRegisterDriver)
        val btnViewDrivers = findViewById<Button>(R.id.btnViewDrivers)
        val btnDeliveredOrdersToQualify = findViewById<Button>(R.id.btnDeliveredOrdersToQualify)
        val btnAllQualifications = findViewById<Button>(R.id.btnAllQualifications)
        val btnOrdersStats = findViewById<Button>(R.id.btnOrdersStats)

        lifecycleScope.launch {
            try {
                val token = tokenManager.getToken()
                if (token.isNullOrBlank()) {
                    goToLogin()
                    return@launch
                }

                val claims = JwtDecoder.extractClaims(token)
                val roles = claims.roles

                when {
                    roles.contains(Roles.ADMIN) -> {
                        btnCreateOrder.visibility = View.GONE
                        btnViewOrders.visibility = View.VISIBLE
                        btnAssignOrders.visibility = View.VISIBLE
                        btnAssignInHub.visibility = View.VISIBLE
                        btnMyOrders.visibility = View.GONE
                        btnRegisterIncident.visibility = View.GONE
                        btnDriverOrders.visibility = View.GONE
                        btnChangeOrderState.visibility = View.GONE
                        btnRegisterDriver.visibility = View.VISIBLE
                        btnViewDrivers.visibility = View.VISIBLE
                        btnAllQualifications.visibility = View.VISIBLE
                        btnOrdersStats.visibility = View.VISIBLE
                    }
                    roles.contains(Roles.REMITENT) -> {
                        btnCreateOrder.visibility = View.VISIBLE
                        btnViewOrders.visibility = View.GONE
                        btnAssignOrders.visibility = View.GONE
                        btnAssignInHub.visibility = View.GONE
                        btnMyOrders.visibility = View.VISIBLE
                        btnRegisterIncident.visibility = View.GONE
                        btnDriverOrders.visibility = View.GONE
                        btnChangeOrderState.visibility = View.GONE
                        btnDeliveredOrdersToQualify.visibility = View.VISIBLE
                    }
                    roles.contains(Roles.DRIVER) -> {
                        btnCreateOrder.visibility = View.GONE
                        btnViewOrders.visibility = View.GONE
                        btnAssignOrders.visibility = View.GONE
                        btnAssignInHub.visibility = View.GONE
                        btnMyOrders.visibility = View.GONE
                        btnRegisterIncident.visibility = View.VISIBLE
                        btnDriverOrders.visibility = View.VISIBLE
                        btnChangeOrderState.visibility = View.VISIBLE
                    }
                }

                // Navegaciones
                btnDriverOrders.setOnClickListener {
                    startActivity(Intent(this@MainMenuActivity, DriverOrdersActivity::class.java))
                }
                btnCreateOrder.setOnClickListener {
                    startActivity(Intent(this@MainMenuActivity, RegisterOrderActivity::class.java))
                }
                btnViewOrders.setOnClickListener {
                    startActivity(Intent(this@MainMenuActivity, GetMyOrdersActivity::class.java))
                }
                btnAssignOrders.setOnClickListener {
                    startActivity(Intent(this@MainMenuActivity, AssignPickupDriverActivity::class.java))
                }
                btnAssignInHub.setOnClickListener {
                    startActivity(Intent(this@MainMenuActivity, AssignDeliveryDriverActivity::class.java))
                }
                btnRegisterIncident.setOnClickListener {
                    startActivity(Intent(this@MainMenuActivity, RegisterIncidentActivity::class.java))
                }
                btnMyOrders.setOnClickListener {
                    startActivity(Intent(this@MainMenuActivity, GetMyOrdersActivity::class.java))
                }
                btnChangeOrderState.setOnClickListener {
                    startActivity(Intent(this@MainMenuActivity, DriverOrdersByStateActivity::class.java))
                }
                btnRegisterDriver.setOnClickListener {
                    startActivity(Intent(this@MainMenuActivity, RegisterDriverActivity::class.java))
                }
                btnViewDrivers.setOnClickListener {
                    startActivity(Intent(this@MainMenuActivity, ViewDriversActivity::class.java))
                }
                btnDeliveredOrdersToQualify.setOnClickListener {
                    startActivity(Intent(this@MainMenuActivity, GetDeliveredOrdersForQualificationActivity::class.java))
                }
                btnAllQualifications.setOnClickListener {
                    startActivity(Intent(this@MainMenuActivity, AllQualificationsActivity::class.java))
                }
                btnOrdersStats.setOnClickListener {
                    startActivity(Intent(this@MainMenuActivity,
                        co.edu.unipiloto.proyectodconvergentes.ui.stats.OrdersStatsActivity::class.java))
                }

                btnLogout.setOnClickListener {
                    lifecycleScope.launch {
                        tokenManager.clear()
                        goToLogin()
                    }
                }

            } catch (e: Exception) {
                toast("Error al cargar menú: ${e.message}")
                goToLogin()
            }
        }
    }
    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
