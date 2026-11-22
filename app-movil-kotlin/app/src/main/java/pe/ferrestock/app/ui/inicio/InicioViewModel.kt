package pe.ferrestock.app.ui.inicio

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pe.ferrestock.app.AppContainer
import pe.ferrestock.app.data.avisos.NotificadorAndroid
import pe.ferrestock.app.data.repository.CajaRepository
import pe.ferrestock.app.data.repository.ProductoRepository
import pe.ferrestock.app.data.repository.ReporteRepository
import pe.ferrestock.app.ui.UiState
import retrofit2.HttpException
import java.io.IOException
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class InicioViewModel(
    private val reporteRepository: ReporteRepository,
    private val cajaRepository: CajaRepository,
    private val productoRepository: ProductoRepository,
    private val notificador: ((String, String) -> Unit)? = null
) : ViewModel() {

    private val _periodo = MutableStateFlow(Periodo.HOY)
    val periodo: StateFlow<Periodo> = _periodo.asStateFlow()

    private val _estado = MutableStateFlow<UiState<InicioEstado>>(UiState.Cargando)
    val estado: StateFlow<UiState<InicioEstado>> = _estado.asStateFlow()

    init {
        cargarDatos()
    }

    fun cambiarPeriodo(nuevoPeriodo: Periodo) {
        _periodo.value = nuevoPeriodo
        cargarDatos()
    }

    fun cargarDatos() {
        viewModelScope.launch {
            _estado.value = UiState.Cargando
            val (desde, hasta) = calcularRangoFechas(_periodo.value)

            val resumenResult = reporteRepository.getResumenVentas(desde, hasta)
            val masVendidosResult = reporteRepository.getProductosMasVendidos(desde, hasta, 5)
            val turnosResult = cajaRepository.getTurnos()
            val ultimaSincResult = productoRepository.getUltimaSincronizacion().first()
            val productos = productoRepository.getProductos().first()

            if (resumenResult.isFailure) {
                _estado.value = UiState.Error(handleError(resumenResult.exceptionOrNull()!!))
                return@launch
            }
            if (masVendidosResult.isFailure) {
                _estado.value = UiState.Error(handleError(masVendidosResult.exceptionOrNull()!!))
                return@launch
            }
            if (turnosResult.isFailure) {
                _estado.value = UiState.Error(handleError(turnosResult.exceptionOrNull()!!))
                return@launch
            }

            val resumen = resumenResult.getOrNull()
            val masVendidos = masVendidosResult.getOrNull() ?: emptyList()
            val turnos = turnosResult.getOrNull() ?: emptyList()

            val turnosConDiferencia = turnos.filter { (it.diferencia ?: 0.0) != 0.0 }
            val productosStockBajoCount = productos.count { it.stock <= it.stockMinimo }

            if (turnosConDiferencia.isNotEmpty()) {
                notificador?.invoke(
                    "Alerta de Caja - Descuadre",
                    "Se detectaron ${turnosConDiferencia.size} turnos cerrados con diferencias monetarias."
                )
            }
            if (productosStockBajoCount > 0) {
                notificador?.invoke(
                    "Alerta de Inventario - Stock Bajo",
                    "Hay $productosStockBajoCount productos con stock igual o inferior al mínimo."
                )
            }

            _estado.value = UiState.Exito(
                InicioEstado(
                    resumen = resumen,
                    masVendidos = masVendidos,
                    turnosConDiferencia = turnosConDiferencia,
                    productosStockBajoCount = productosStockBajoCount,
                    ultimaSincronizacion = ultimaSincResult
                )
            )
        }
    }

    private fun calcularRangoFechas(p: Periodo): Pair<String, String> {
        val hoy = LocalDate.now()
        val formato = DateTimeFormatter.ISO_LOCAL_DATE
        val hastaStr = hoy.format(formato)
        val desdeStr = when (p) {
            Periodo.HOY -> hastaStr
            Periodo.SEMANA -> hoy.with(DayOfWeek.MONDAY).format(formato)
            Periodo.MES -> hoy.withDayOfMonth(1).format(formato)
        }
        return Pair(desdeStr, hastaStr)
    }

    private fun handleError(error: Throwable): String {
        return when (error) {
            is IOException -> "No hay conexión con el servidor"
            is HttpException -> if (error.code() == 401) "Tu sesión expiró, vuelve a ingresar" else "El servidor respondió con error ${error.code()}"
            else -> error.message ?: "Error desconocido"
        }
    }

    class Factory(
        private val context: Context,
        private val container: AppContainer
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return InicioViewModel(
                reporteRepository = container.reporteRepository,
                cajaRepository = container.cajaRepository,
                productoRepository = container.productoRepository,
                notificador = { title, message ->
                    NotificadorAndroid.enviarNotificacion(context.applicationContext, title, message)
                }
            ) as T
        }
    }
}
