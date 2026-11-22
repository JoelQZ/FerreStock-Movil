package pe.ferrestock.app.ui.inicio

import pe.ferrestock.app.domain.model.ProductoMasVendidoVista
import pe.ferrestock.app.domain.model.ResumenVentaVista
import pe.ferrestock.app.domain.model.TurnoVista

data class InicioEstado(
    val resumen: ResumenVentaVista?,
    val masVendidos: List<ProductoMasVendidoVista>,
    val turnosConDiferencia: List<TurnoVista>,
    val productosStockBajoCount: Int,
    val ultimaSincronizacion: String?
)
