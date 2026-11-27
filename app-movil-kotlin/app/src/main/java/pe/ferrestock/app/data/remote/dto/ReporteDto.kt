package pe.ferrestock.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MetodoPagoDto(
    val metodo: String = "",
    @SerialName("cantidad_ventas") val cantidadVentas: Int = 0,
    val total: String = "0.00"
)

@Serializable
data class ResumenReporteDto(
    @SerialName("cantidad_ventas") val cantidadVentas: Int? = null,
    @SerialName("cant_ventas") val cantVentas: Int? = null,
    val total: String? = "0.00"
)

@Serializable
data class ReporteVentasDto(
    @SerialName("cantidad_ventas") val cantidadVentas: Int? = null,
    @SerialName("cant_ventas") val cantVentas: Int? = null,
    val total: String? = null,
    @SerialName("por_metodo_pago") val porMetodoPago: List<MetodoPagoDto> = emptyList(),
    val resumen: ResumenReporteDto? = null
)

@Serializable
data class ProductoMasVendidoDto(
    @SerialName("producto_id") val productoId: Int = 0,
    val producto: Int? = null,
    val codigo: String = "",
    val nombre: String = "",
    @SerialName("cantidad_vendida") @Serializable(with = EnteroFlexible::class) val cantidadVendida: Int = 0,
    @SerialName("total_vendido") val totalVendido: String = "0.00"
)

@Serializable
data class ProductosMasVendidosResponseDto(
    val productos: List<ProductoMasVendidoDto> = emptyList()
)
