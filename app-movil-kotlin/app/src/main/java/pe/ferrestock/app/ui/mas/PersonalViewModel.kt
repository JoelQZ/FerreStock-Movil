package pe.ferrestock.app.ui.mas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pe.ferrestock.app.AppContainer
import pe.ferrestock.app.data.repository.ProductoRepository
import pe.ferrestock.app.data.repository.UsuarioRepository
import pe.ferrestock.app.domain.model.UsuarioVista
import pe.ferrestock.app.ui.UiState
import retrofit2.HttpException
import java.io.IOException

class PersonalViewModel(
    private val usuarioRepository: UsuarioRepository,
    private val productoRepository: ProductoRepository
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<List<UsuarioVista>>>(UiState.Cargando)
    val estado: StateFlow<UiState<List<UsuarioVista>>> = _estado.asStateFlow()

    private val _ultimaSincronizacion = MutableStateFlow<String?>(null)
    val ultimaSincronizacion: StateFlow<String?> = _ultimaSincronizacion.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _estado.value = UiState.Cargando
            val usuariosResult = usuarioRepository.getUsuarios()
            val ultimaSinc = productoRepository.getUltimaSincronizacion().first()
            _ultimaSincronizacion.value = ultimaSinc

            if (usuariosResult.isFailure) {
                _estado.value = UiState.Error(handleError(usuariosResult.exceptionOrNull()!!))
                return@launch
            }

            val usuarios = usuariosResult.getOrNull() ?: emptyList()
            _estado.value = UiState.Exito(usuarios)
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
            return PersonalViewModel(
                usuarioRepository = container.usuarioRepository,
                productoRepository = container.productoRepository
            ) as T
        }
    }
}
