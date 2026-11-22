package pe.ferrestock.app.domain.model

data class MetodoPagoVista(
    val metodo: String,
    val cantidadVentas: Int,
    val total: Double
)

data class ResumenVentaVista(
    val cantidadVentas: Int,
    val total: Double,
    val porMetodoPago: List<MetodoPagoVista> = emptyList()
)

data class ProductoMasVendidoVista(
    val productoId: Int,
    val codigo: String,
    val nombre: String,
    val cantidadVendida: Int,
    val totalVendido: Double
)

data class TurnoVista(
    val idTurno: Int,
    val usuarioNombre: String,
    val apertura: String,
    val montoInicial: Double,
    val cierre: String? = null,
    val montoEsperado: Double? = null,
    val montoReal: Double? = null,
    val diferencia: Double? = null,
    val estado: String
)

data class VentaVista(
    val idVenta: Int,
    val fechaHora: String,
    val total: Double,
    val estado: String,
    val usuarioNombre: String
)

data class PrediccionVista(
    val productoId: Int,
    val productoCodigo: String,
    val productoNombre: String,
    val stockActual: Int,
    val stockMinimo: Int,
    val demandaEstimada: Int,
    val diasParaAgotarse: Int,
    val cantidadSugerida: Int
)

data class MovimientoVista(
    val productoId: Int,
    val tipo: String,
    val cantidad: Int,
    val stockAnterior: Int,
    val stockNuevo: Int,
    val fecha: String,
    val usuarioNombre: String
)

data class ProveedorVista(
    val idProveedor: Int,
    val ruc: String,
    val razonSocial: String,
    val estado: Boolean
)

data class UsuarioVista(
    val id: Int,
    val nombre: String,
    val usuario: String,
    val rolNombre: String,
    val estado: Boolean,
    val lastLogin: String? = null
)
