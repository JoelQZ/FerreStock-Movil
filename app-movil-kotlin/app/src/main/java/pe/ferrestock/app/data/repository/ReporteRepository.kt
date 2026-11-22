package pe.ferrestock.app.data.repository

import pe.ferrestock.app.domain.model.ProductoMasVendidoVista
import pe.ferrestock.app.domain.model.ResumenVentaVista

interface ReporteRepository {
    suspend fun getResumenVentas(desde: String, hasta: String): Result<ResumenVentaVista>
    suspend fun getProductosMasVendidos(desde: String, hasta: String, limite: Int = 5): Result<List<ProductoMasVendidoVista>>
}
