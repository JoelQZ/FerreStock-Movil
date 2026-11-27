package pe.ferrestock.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("usuario") val username: String,
    val password: String
)

@Serializable
data class UsuarioLoginDto(
    val id: Int? = null,
    val nombre: String? = null,
    val usuario: String? = null,
    val rol: String? = null
)

@Serializable
data class LoginResponse(
    val access: String,
    val refresh: String? = null,
    val usuario: UsuarioLoginDto? = null
)
