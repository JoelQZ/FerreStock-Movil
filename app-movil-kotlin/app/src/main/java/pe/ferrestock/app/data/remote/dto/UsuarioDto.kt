package pe.ferrestock.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UsuarioDto(
    val id: Int = 0,
    val nombre: String? = null,
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("last_name") val lastName: String? = null,
    val usuario: String? = null,
    val username: String? = null,
    @SerialName("rol_nombre") val rolNombre: String? = null,
    @Serializable(with = TextoFlexible::class) val rol: String? = null,
    val estado: Boolean = true,
    @SerialName("is_active") val isActive: Boolean? = null,
    @SerialName("last_login") val lastLogin: String? = null
)
