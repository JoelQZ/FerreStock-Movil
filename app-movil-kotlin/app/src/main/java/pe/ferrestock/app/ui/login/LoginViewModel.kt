package pe.ferrestock.app.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.ferrestock.app.AppContainer
import pe.ferrestock.app.data.repository.AuthRepository
import pe.ferrestock.app.domain.model.Rol
import pe.ferrestock.app.domain.model.Sesion
import pe.ferrestock.app.ui.UiState
import retrofit2.HttpException
import java.io.IOException

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<Sesion>>(UiState.Cargando)
    val estado: StateFlow<UiState<Sesion>> = _estado.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _estado.value = UiState.Cargando
            val result = authRepository.login(username, password)
            result.fold(
                onSuccess = { sesion ->
                    if (sesion.rol == Rol.CAJERO) {
                        _estado.value = UiState.Error("Tu cuenta es de caja. Usa el punto de venta")
                    } else if (sesion.rol == Rol.ADMINISTRADOR) {
                        _estado.value = UiState.Exito(sesion)
                    } else {
                        _estado.value = UiState.Error("Rol no autorizado")
                    }
                },
                onFailure = { error ->
                    val mensaje = when (error) {
                        is IOException -> "No hay conexión con el servidor"
                        is HttpException -> if (error.code() == 401) {
                            "Tu sesión expiró, vuelve a ingresar"
                        } else {
                            "El servidor respondió con error ${error.code()}"
                        }
                        else -> error.message ?: "Error al iniciar sesión"
                    }
                    _estado.value = UiState.Error(mensaje)
                }
            )
        }
    }

    class Factory(private val container: AppContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LoginViewModel(container.authRepository) as T
        }
    }
}
