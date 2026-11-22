package pe.ferrestock.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TurnoDto(
    @SerialName("id_turno") val idTurno: Int? = null,
    val id: Int? = null,
    @SerialName("usuario_nombre") val usuarioNombre: String? = null,
    val usuario: String? = null,
    val apertura: String = "",
    @SerialName("monto_inicial") val montoInicial: String = "0.00",
    val cierre: String? = null,
    @SerialName("monto_esperado") val montoEsperado: String? = null,
    @SerialName("monto_real") val montoReal: String? = null,
    val diferencia: String? = null,
    val estado: String = "ABIERTO"
)

@Serializable
data class ResumenTurnoDto(
    @SerialName("monto_esperado") val montoEsperado: String = "0.00"
)

@Serializable
data class VentaDto(
    @SerialName("id_venta") val idVenta: Int? = null,
    val id: Int? = null,
    @SerialName("fecha_hora") val fechaHora: String? = null,
    val fecha: String? = null,
    val total: String = "0.00",
    val estado: String = "COMPLETADA",
    @SerialName("usuario_nombre") val usuarioNombre: String? = null,
    val usuario: String? = null
)
