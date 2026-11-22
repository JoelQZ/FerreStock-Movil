package pe.ferrestock.app.ui.stock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pe.ferrestock.app.AppContainer
import pe.ferrestock.app.data.repository.InventarioRepository
import pe.ferrestock.app.data.repository.ProductoRepository
import pe.ferrestock.app.ui.UiState
import retrofit2.HttpException
import java.io.IOException

class StockViewModel(
    private val productoRepository: ProductoRepository,
    private val inventarioRepository: InventarioRepository
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<StockEstado>>(UiState.Cargando)
    val estado: StateFlow<UiState<StockEstado>> = _estado.asStateFlow()

    private val _aviso = MutableStateFlow<String?>(null)
    val aviso: StateFlow<String?> = _aviso.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _estado.value = UiState.Cargando
            try {
                combine(
                    productoRepository.getProductos(),
                    inventarioRepository.getPredicciones(),
                    productoRepository.getUltimaSincronizacion()
                ) { productos, predicciones, ultimaSinc ->
                    StockEstado(
                        productos = productos,
                        predicciones = predicciones,
                        ultimaSincronizacion = ultimaSinc
                    )
                }.collect { stockEstado ->
                    _estado.value = UiState.Exito(stockEstado)
                }
            } catch (e: Exception) {
                _estado.value = UiState.Error(handleError(e))
            }
        }
    }

    fun sincronizar() {
        viewModelScope.launch {
            _aviso.value = null
            val prodResult = productoRepository.refrescar()
            val predResult = inventarioRepository.refrescarPredicciones()

            if (prodResult.isFailure || predResult.isFailure) {
                val err = prodResult.exceptionOrNull() ?: predResult.exceptionOrNull()
                if (err is IOException) {
                    _aviso.value = "Sin conexión: mostrando datos guardados"
                } else if (err is HttpException) {
                    _aviso.value = if (err.code() == 401) "Tu sesión expiró, vuelve a ingresar" else "El servidor respondió con error ${err.code()}"
                } else {
                    _aviso.value = err?.message ?: "Error al sincronizar"
                }
            }
        }
    }

    private fun handleError(error: Throwable): String {
        return when (error) {
            is IOException -> "No hay conexión con el servidor"
            is HttpException -> if (error.code() == 401) "Tu sesión expiró, vuelve a ingresar" else "El servidor respondió con error ${error.code()}"
            else -> error.message ?: "Error desconocido"
        }
    }

    class Factory(private val container: AppContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return StockViewModel(
                productoRepository = container.productoRepository,
                inventarioRepository = container.inventarioRepository
            ) as T
        }
    }
}
