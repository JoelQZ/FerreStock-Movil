package pe.ferrestock.app

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import pe.ferrestock.app.data.repository.InventarioRepository
import pe.ferrestock.app.data.repository.ProductoRepository
import pe.ferrestock.app.data.remote.dto.DetalleIngresoDto
import pe.ferrestock.app.data.remote.dto.IngresoResponseDto
import pe.ferrestock.app.domain.model.MovimientoVista
import pe.ferrestock.app.domain.model.PrediccionVista
import pe.ferrestock.app.domain.model.Producto
import pe.ferrestock.app.domain.model.ProveedorVista
import pe.ferrestock.app.ui.recibir.RecibirViewModel

class FakeInventarioRepositoryForRecibir : InventarioRepository {
    var proveedoresResult: Result<List<ProveedorVista>> = Result.success(listOf(ProveedorVista(1, "10203040501", "Proveedor S.A.C.", true)))
    var registrarIngresoResult: Result<IngresoResponseDto> = Result.success(IngresoResponseDto(1, "Registrado"))
    var registrarIngresoCalled = false

    override fun getPredicciones(): Flow<List<PrediccionVista>> = flowOf(emptyList())
    override suspend fun refrescarPredicciones(): Result<Unit> = Result.success(Unit)
    override suspend fun getKardex(): Result<List<MovimientoVista>> = Result.success(emptyList())
    override suspend fun getProveedores(): Result<List<ProveedorVista>> = proveedoresResult
    override suspend fun registrarIngreso(proveedorId: Int, nroDocumento: String, detalles: List<DetalleIngresoDto>): Result<IngresoResponseDto> {
        registrarIngresoCalled = true
        return registrarIngresoResult
    }
}

class FakeProductoRepositoryForRecibir : ProductoRepository {
    var refrescarCount = 0

    override fun getProductos(): Flow<List<Producto>> = flowOf(emptyList())
    override suspend fun refrescar(): Result<Unit> {
        refrescarCount++
        return Result.success(Unit)
    }
    override fun getUltimaSincronizacion(): Flow<String?> = flowOf(null)
}

class RecibirViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val inventarioRepo = FakeInventarioRepositoryForRecibir()
    private val productoRepo = FakeProductoRepositoryForRecibir()

    @Test
    fun registroExitosoRefrescaStock() = runTest {
        val viewModel = RecibirViewModel(inventarioRepo, productoRepo)
        val detalles = listOf(DetalleIngresoDto(producto = 1, cantidad = "10", costoUnitario = "15.00"))

        viewModel.registrar(proveedorId = 1, nroDocumento = "F001-0001", detalles = detalles)

        assertTrue(inventarioRepo.registrarIngresoCalled)
        assertEquals(1, productoRepo.refrescarCount)
    }
}
