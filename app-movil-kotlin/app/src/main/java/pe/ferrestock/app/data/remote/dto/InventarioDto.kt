package pe.ferrestock.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PrediccionDto(
    val producto: Int? = null,
    @SerialName("producto_id") val productoId: Int? = null,
    @SerialName("producto_codigo") val productoCodigo: String = "",
    @SerialName("producto_nombre") val productoNombre: String = "",
    @SerialName("stock_actual") val stockActual: Int = 0,
    @SerialName("stock_minimo") val stockMinimo: Int = 0,
    @SerialName("demanda_estimada") val demandaEstimada: Int = 0,
    @SerialName("dias_para_agotarse") val diasParaAgotarse: Int = 0,
    @SerialName("cantidad_sugerida") val cantidadSugerida: Int = 0
)

@Serializable
data class MovimientoKardexDto(
    val producto: Int = 0,
    @SerialName("producto_id") val productoId: Int? = null,
    val tipo: String = "",
    val cantidad: Int = 0,
    @SerialName("stock_anterior") val stockAnterior: Int = 0,
    @SerialName("stock_nuevo") val stockNuevo: Int = 0,
    val fecha: String = "",
    @SerialName("usuario_nombre") val usuarioNombre: String? = null,
    val usuario: String? = null
)

@Serializable
data class ProveedorDto(
    @SerialName("id_proveedor") val idProveedor: Int? = null,
    val id: Int? = null,
    val ruc: String = "",
    @SerialName("razon_social") val razonSocial: String = "",
    val estado: Boolean = true
)

@Serializable
data class DetalleIngresoDto(
    val producto: Int,
    val cantidad: String,
    @SerialName("costo_unitario") val costoUnitario: String
)

@Serializable
data class IngresoRequestDto(
    val proveedor: Int,
    @SerialName("nro_documento") val nroDocumento: String,
    val detalles: List<DetalleIngresoDto>
)

@Serializable
data class IngresoResponseDto(
    val id: Int? = null,
    val mensaje: String? = null
)
