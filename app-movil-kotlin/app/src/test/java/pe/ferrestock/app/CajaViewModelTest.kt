package pe.ferrestock.app

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import pe.ferrestock.app.data.repository.CajaRepository
import pe.ferrestock.app.domain.model.TurnoVista
import pe.ferrestock.app.domain.model.VentaVista
import pe.ferrestock.app.ui.UiState
import pe.ferrestock.app.ui.caja.CajaViewModel
import java.io.IOException

class FakeCajaRepository : CajaRepository {
    var turnosResult: Result<List<TurnoVista>> = Result.success(emptyList())
    var ventasAnuladasResult: Result<List<VentaVista>> = Result.success(emptyList())
    var resumenTurnoResult: Result<Double> = Result.success(0.0)

    override suspend fun getTurnos(): Result<List<TurnoVista>> = turnosResult
    override suspend fun getResumenTurno(id: Int): Result<Double> = resumenTurnoResult
    override suspend fun getVentasAnuladas(): Result<List<VentaVista>> = ventasAnuladasResult
}

class CajaViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeCajaRepository = FakeCajaRepository()

    @Test
    fun separaAbiertosYCerrados() = runTest {
        val turnoAbierto = TurnoVista(
            idTurno = 1,
            usuarioNombre = "Admin",
            apertura = "Hoy 08:00",
            montoInicial = 100.0,
            cierre = null,
            estado = "ABIERTO"
        )
        val turnoCerrado = TurnoVista(
            idTurno = 2,
            usuarioNombre = "Admin",
            apertura = "Ayer 08:00",
            montoInicial = 100.0,
            cierre = "Ayer 18:00",
            montoEsperado = 500.0,
            montoReal = 500.0,
            diferencia = 0.0,
            estado = "CERRADO"
        )

        fakeCajaRepository.turnosResult = Result.success(listOf(turnoAbierto, turnoCerrado))

        val viewModel = CajaViewModel(fakeCajaRepository)
        val estado = viewModel.estado.value

        assertTrue(estado is UiState.Exito)
        val cajaEstado = (estado as UiState.Exito).datos
        assertEquals(1, cajaEstado.turnosAbiertos.size)
        assertEquals(1, cajaEstado.turnosCerrados.size)
        assertEquals(turnoAbierto.idTurno, cajaEstado.turnosAbiertos[0].idTurno)
        assertEquals(turnoCerrado.idTurno, cajaEstado.turnosCerrados[0].idTurno)
    }

    @Test
    fun errorDeRedMuestraError() = runTest {
        fakeCajaRepository.turnosResult = Result.failure(IOException("Network failure"))

        val viewModel = CajaViewModel(fakeCajaRepository)
        val estado = viewModel.estado.value

        assertTrue(estado is UiState.Error)
        assertEquals("No hay conexión con el servidor", (estado as UiState.Error).mensaje)
    }
}
