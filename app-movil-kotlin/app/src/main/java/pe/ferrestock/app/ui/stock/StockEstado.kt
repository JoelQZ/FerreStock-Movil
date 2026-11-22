package pe.ferrestock.app.ui.stock

import pe.ferrestock.app.domain.model.PrediccionVista
import pe.ferrestock.app.domain.model.Producto

data class StockEstado(
    val productos: List<Producto>,
    val predicciones: List<PrediccionVista>,
    val ultimaSincronizacion: String?
)
