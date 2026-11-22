package pe.ferrestock.app.ui.caja

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.ferrestock.app.AppContainer
import pe.ferrestock.app.data.repository.CajaRepository
import pe.ferrestock.app.ui.UiState
import retrofit2.HttpException
import java.io.IOException

class CajaViewModel(
    private val cajaRepository: CajaRepository
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<CajaEstado>>(UiState.Cargando)
    val estado: StateFlow<UiState<CajaEstado>> = _estado.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _estado.value = UiState.Cargando

            val turnosResult = cajaRepository.getTurnos()
            val ventasAnuladasResult = cajaRepository.getVentasAnuladas()

            if (turnosResult.isFailure) {
                _estado.value = UiState.Error(handleError(turnosResult.exceptionOrNull()!!))
                return@launch
            }
            if (ventasAnuladasResult.isFailure) {
                _estado.value = UiState.Error(handleError(ventasAnuladasResult.exceptionOrNull()!!))
                return@launch
            }

            val turnos = turnosResult.getOrNull() ?: emptyList()
            val ventasAnuladas = ventasAnuladasResult.getOrNull() ?: emptyList()

            val turnosAbiertos = turnos.filter { it.cierre.isNullOrBlank() || it.estado.equals("ABIERTO", ignoreCase = true) }
            val turnosCerrados = turnos.filter { !it.cierre.isNullOrBlank() || it.estado.equals("CERRADO", ignoreCase = true) }

            _estado.value = UiState.Exito(
                CajaEstado(
                    turnosAbiertos = turnosAbiertos,
                    turnosCerrados = turnosCerrados,
                    ventasAnuladas = ventasAnuladas
                )
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
            return CajaViewModel(container.cajaRepository) as T
        }
    }
}
