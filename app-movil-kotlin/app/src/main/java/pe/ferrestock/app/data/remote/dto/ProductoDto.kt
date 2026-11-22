package pe.ferrestock.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductoDto(
    val id: Int,
    val codigo: String,
    val nombre: String,
    val precio: String,
    val stock: Int,
    @SerialName("stock_minimo") val stockMinimo: Int
)
