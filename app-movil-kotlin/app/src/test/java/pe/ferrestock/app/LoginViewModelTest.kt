package pe.ferrestock.app

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import pe.ferrestock.app.data.repository.AuthRepository
import pe.ferrestock.app.domain.model.Rol
import pe.ferrestock.app.domain.model.Sesion
import pe.ferrestock.app.ui.UiState
import pe.ferrestock.app.ui.login.LoginViewModel
import java.io.IOException

class FakeAuthRepository : AuthRepository {
    var resultadoLogin: Result<Sesion>? = null

    override suspend fun login(username: String, password: String): Result<Sesion> {
        return resultadoLogin ?: Result.failure(Exception("Sin resultado configurado"))
    }
}

class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeAuthRepository = FakeAuthRepository()

    @Test
    fun loginExitosoAdministradorPasaAExito() = runTest {
        val sesionAdmin = Sesion(
            token = "jwt_token_123",
            usuario = "admin",
            rol = Rol.ADMINISTRADOR
        )
        fakeAuthRepository.resultadoLogin = Result.success(sesionAdmin)

        val viewModel = LoginViewModel(fakeAuthRepository)
        viewModel.login("admin", "123456")

        val estado = viewModel.estado.value
        assertTrue(estado is UiState.Exito)
        assertEquals(sesionAdmin, (estado as UiState.Exito).datos)
    }

    @Test
    fun loginCajeroRechazadoConMensajeEspecifico() = runTest {
        val sesionCajero = Sesion(
            token = "jwt_token_456",
            usuario = "cajero",
            rol = Rol.CAJERO
        )
        fakeAuthRepository.resultadoLogin = Result.success(sesionCajero)

        val viewModel = LoginViewModel(fakeAuthRepository)
        viewModel.login("cajero", "123456")

        val estado = viewModel.estado.value
        assertTrue(estado is UiState.Error)
        assertEquals(
            "Tu cuenta es de caja. Usa el punto de venta",
            (estado as UiState.Error).mensaje
        )
    }

    @Test
    fun loginErrorDeRedMuestraErrorSinConexion() = runTest {
        fakeAuthRepository.resultadoLogin = Result.failure(IOException("Network error"))

        val viewModel = LoginViewModel(fakeAuthRepository)
        viewModel.login("admin", "123456")

        val estado = viewModel.estado.value
        assertTrue(estado is UiState.Error)
        assertEquals(
            "No hay conexión con el servidor",
            (estado as UiState.Error).mensaje
        )
    }
}
