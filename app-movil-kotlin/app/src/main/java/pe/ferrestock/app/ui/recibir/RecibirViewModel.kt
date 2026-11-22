package pe.ferrestock.app.ui.recibir

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pe.ferrestock.app.AppContainer
import pe.ferrestock.app.data.remote.dto.DetalleIngresoDto
import pe.ferrestock.app.data.repository.InventarioRepository
import pe.ferrestock.app.data.repository.ProductoRepository
import pe.ferrestock.app.ui.UiState
import retrofit2.HttpException
import java.io.IOException

class RecibirViewModel(
    private val inventarioRepository: InventarioRepository,
    private val productoRepository: ProductoRepository
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<RecibirEstado>>(UiState.Cargando)
    val estado: StateFlow<UiState<RecibirEstado>> = _estado.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _estado.value = UiState.Cargando
            val provResult = inventarioRepository.getProveedores()
            val productosFlow = productoRepository.getProductos().first()

            if (provResult.isFailure) {
                _estado.value = UiState.Error(handleError(provResult.exceptionOrNull()!!))
                return@launch
            }

            val proveedores = provResult.getOrNull() ?: emptyList()
            _estado.value = UiState.Exito(
                RecibirEstado(
                    proveedores = proveedores,
                    productos = productosFlow
                )
            )
        }
    }

    fun registrar(proveedorId: Int, nroDocumento: String, detalles: List<DetalleIngresoDto>) {
        viewModelScope.launch {
            _mensaje.value = null
            val result = inventarioRepository.registrarIngreso(proveedorId, nroDocumento, detalles)
            result.fold(
                onSuccess = {
                    _mensaje.value = "Ingreso registrado con éxito"
                    productoRepository.refrescar()
                    cargar()
                },
                onFailure = { error ->
                    _mensaje.value = handleError(error)
                }
            )
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
            return RecibirViewModel(
                inventarioRepository = container.inventarioRepository,
                productoRepository = container.productoRepository
            ) as T
        }
    }
}
