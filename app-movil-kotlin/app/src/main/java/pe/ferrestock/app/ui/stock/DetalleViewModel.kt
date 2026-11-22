package pe.ferrestock.app.ui.stock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pe.ferrestock.app.AppContainer
import pe.ferrestock.app.data.repository.InventarioRepository
import pe.ferrestock.app.data.repository.ProductoRepository
import pe.ferrestock.app.ui.UiState
import retrofit2.HttpException
import java.io.IOException

class DetalleViewModel(
    private val productoId: Int,
    private val productoRepository: ProductoRepository,
    private val inventarioRepository: InventarioRepository
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<DetalleEstado>>(UiState.Cargando)
    val estado: StateFlow<UiState<DetalleEstado>> = _estado.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _estado.value = UiState.Cargando
            try {
                val productos = productoRepository.getProductos().first()
                val producto = productos.find { it.id == productoId }

                val kardexResult = inventarioRepository.getKardex()
                if (kardexResult.isFailure) {
                    _estado.value = UiState.Error(handleError(kardexResult.exceptionOrNull()!!))
                    return@launch
                }

                val movimientos = kardexResult.getOrNull() ?: emptyList()
                val movimientosFiltrados = movimientos.filter { it.productoId == productoId }

                _estado.value = UiState.Exito(
                    DetalleEstado(
                        producto = producto,
                        movimientos = movimientosFiltrados
                    )
                )
            } catch (e: Exception) {
                _estado.value = UiState.Error(handleError(e))
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

    class Factory(
        private val productoId: Int,
        private val container: AppContainer
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DetalleViewModel(
                productoId = productoId,
                productoRepository = container.productoRepository,
                inventarioRepository = container.inventarioRepository
            ) as T
        }
    }
}
