package pe.ferrestock.app.data.repository

import kotlinx.coroutines.flow.Flow
import pe.ferrestock.app.data.remote.dto.DetalleIngresoDto
import pe.ferrestock.app.data.remote.dto.IngresoResponseDto
import pe.ferrestock.app.domain.model.MovimientoVista
import pe.ferrestock.app.domain.model.PrediccionVista
import pe.ferrestock.app.domain.model.ProveedorVista

interface InventarioRepository {
    fun getPredicciones(): Flow<List<PrediccionVista>>
    suspend fun refrescarPredicciones(): Result<Unit>
    suspend fun getKardex(): Result<List<MovimientoVista>>
    suspend fun getProveedores(): Result<List<ProveedorVista>>
    suspend fun registrarIngreso(proveedorId: Int, nroDocumento: String, detalles: List<DetalleIngresoDto>): Result<IngresoResponseDto>
}
