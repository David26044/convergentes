package co.edu.unipiloto.proyectodconvergentes.ui.net

object Roles {
    const val ADMIN = "ROLE_ADMIN"
    const val DRIVER = "ROLE_DRIVER"
    const val REMITENT = "ROLE_REMITENT"
}

fun List<String>.hasAnyRole(vararg targets: String): Boolean =
    this.any { it in targets }
