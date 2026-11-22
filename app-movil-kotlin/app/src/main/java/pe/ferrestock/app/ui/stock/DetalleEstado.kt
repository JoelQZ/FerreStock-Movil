package pe.ferrestock.app.ui.stock

import pe.ferrestock.app.domain.model.MovimientoVista
import pe.ferrestock.app.domain.model.Producto

data class DetalleEstado(
    val producto: Producto?,
    val movimientos: List<MovimientoVista>
)
