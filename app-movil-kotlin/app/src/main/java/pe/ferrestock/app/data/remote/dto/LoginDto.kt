package pe.ferrestock.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val access: String,
    val refresh: String? = null,
    val usuario: String? = null,
    val username: String? = null,
    val rol: String? = null,
    @SerialName("rol_nombre") val rolNombre: String? = null
)
