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
import pe.ferrestock.app.ui.UiState
import pe.ferrestock.app.ui.stock.StockViewModel
import java.io.IOException

class FakeProductoRepositoryForStock : ProductoRepository {
    var productosRoom = listOf(Producto(1, "P01", "Martillo", 25.0, 10, 5))
    var refrescarResult: Result<Unit> = Result.success(Unit)

    override fun getProductos(): Flow<List<Producto>> = flowOf(productosRoom)
    override suspend fun refrescar(): Result<Unit> = refrescarResult
    override fun getUltimaSincronizacion(): Flow<String?> = flowOf("Hoy 10:00")
}

class FakeInventarioRepositoryForStock : InventarioRepository {
    var prediccionesRoom = listOf(PrediccionVista(1, "P01", "Martillo", 10, 5, 12, 4, 10))
    var refrescarPredResult: Result<Unit> = Result.success(Unit)

    override fun getPredicciones(): Flow<List<PrediccionVista>> = flowOf(prediccionesRoom)
    override suspend fun refrescarPredicciones(): Result<Unit> = refrescarPredResult
    override suspend fun getKardex(): Result<List<MovimientoVista>> = Result.success(emptyList())
    override suspend fun getProveedores(): Result<List<ProveedorVista>> = Result.success(emptyList())
    override suspend fun registrarIngreso(proveedorId: Int, nroDocumento: String, detalles: List<DetalleIngresoDto>): Result<IngresoResponseDto> =
        Result.success(IngresoResponseDto(1, "OK"))
}

class StockViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val productoRepo = FakeProductoRepositoryForStock()
    private val inventarioRepo = FakeInventarioRepositoryForStock()

    @Test
    fun sinInternetUsaRoom() = runTest {
        productoRepo.refrescarResult = Result.failure(IOException("No internet"))
        inventarioRepo.refrescarPredResult = Result.failure(IOException("No internet"))

        val viewModel = StockViewModel(productoRepo, inventarioRepo)
        viewModel.sincronizar()

        val estado = viewModel.estado.value
        assertTrue(estado is UiState.Exito)
        assertEquals("Sin conexión: mostrando datos guardados", viewModel.aviso.value)
        assertEquals(1, (estado as UiState.Exito).datos.productos.size)
        assertEquals(1, estado.datos.predicciones.size)
    }
}
