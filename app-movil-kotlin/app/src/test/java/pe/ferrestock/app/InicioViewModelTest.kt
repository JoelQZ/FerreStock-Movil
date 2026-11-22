package pe.ferrestock.app

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import pe.ferrestock.app.data.repository.CajaRepository
import pe.ferrestock.app.data.repository.ProductoRepository
import pe.ferrestock.app.data.repository.ReporteRepository
import pe.ferrestock.app.domain.model.Producto
import pe.ferrestock.app.domain.model.ProductoMasVendidoVista
import pe.ferrestock.app.domain.model.ResumenVentaVista
import pe.ferrestock.app.domain.model.TurnoVista
import pe.ferrestock.app.domain.model.VentaVista
import pe.ferrestock.app.ui.UiState
import pe.ferrestock.app.ui.inicio.InicioViewModel
import pe.ferrestock.app.ui.inicio.Periodo
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FakeReporteRepositoryForInicio : ReporteRepository {
    var resumenResult: Result<ResumenVentaVista> = Result.success(ResumenVentaVista(10, 1500.0))
    var masVendidosResult: Result<List<ProductoMasVendidoVista>> = Result.success(emptyList())
    var ultimaDesde: String = ""
    var ultimaHasta: String = ""

    override suspend fun getResumenVentas(desde: String, hasta: String): Result<ResumenVentaVista> {
        ultimaDesde = desde
        ultimaHasta = hasta
        return resumenResult
    }

    override suspend fun getProductosMasVendidos(desde: String, hasta: String, limite: Int): Result<List<ProductoMasVendidoVista>> {
        ultimaDesde = desde
        ultimaHasta = hasta
        return masVendidosResult
    }
}

class FakeCajaRepositoryForInicio : CajaRepository {
    var turnosResult: Result<List<TurnoVista>> = Result.success(emptyList())
    override suspend fun getTurnos(): Result<List<TurnoVista>> = turnosResult
    override suspend fun getResumenTurno(id: Int): Result<Double> = Result.success(0.0)
    override suspend fun getVentasAnuladas(): Result<List<VentaVista>> = Result.success(emptyList())
}

class FakeProductoRepositoryForInicio : ProductoRepository {
    override fun getProductos(): Flow<List<Producto>> = flowOf(emptyList())
    override suspend fun refrescar(): Result<Unit> = Result.success(Unit)
    override fun getUltimaSincronizacion(): Flow<String?> = flowOf(null)
}

class InicioViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val reporteRepo = FakeReporteRepositoryForInicio()
    private val cajaRepo = FakeCajaRepositoryForInicio()
    private val productoRepo = FakeProductoRepositoryForInicio()

    @Test
    fun cambiarPeriodoRecarga() = runTest {
        val viewModel = InicioViewModel(reporteRepo, cajaRepo, productoRepo)
        viewModel.cambiarPeriodo(Periodo.MES)

        val hoy = LocalDate.now()
        val primerDiaMes = hoy.withDayOfMonth(1).format(DateTimeFormatter.ISO_LOCAL_DATE)

        assertEquals(primerDiaMes, reporteRepo.ultimaDesde)
        assertTrue(viewModel.estado.value is UiState.Exito)
    }
}
