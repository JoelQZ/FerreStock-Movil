package pe.ferrestock.app.data.remote.dto

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.JsonNames
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class ProductoDto(
    @JsonNames("id_producto") val id: Int,
    val codigo: String,
    val nombre: String,
    @JsonNames("precio_venta") val precio: String,
    @Serializable(with = EnteroFlexible::class) val stock: Int,
    @SerialName("stock_minimo") @Serializable(with = EnteroFlexible::class) val stockMinimo: Int
)
