package pe.ferrestock.app.domain.model

data class Sesion(
    val token: String,
    val usuario: String,
    val rol: Rol,
    val firebaseToken: String? = null
)
