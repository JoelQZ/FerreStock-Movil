package pe.ferrestock.app.data.repository

import kotlinx.coroutines.flow.Flow
import pe.ferrestock.app.domain.model.Producto

interface ProductoRepository {
    fun getProductos(): Flow<List<Producto>>
    suspend fun refrescar(): Result<Unit>
    fun getUltimaSincronizacion(): Flow<String?>
}
