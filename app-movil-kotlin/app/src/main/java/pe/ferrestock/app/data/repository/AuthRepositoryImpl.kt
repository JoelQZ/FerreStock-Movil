package pe.ferrestock.app.data.repository

import pe.ferrestock.app.data.mapper.toDomain
import pe.ferrestock.app.data.remote.ApiService
import pe.ferrestock.app.data.remote.dto.LoginRequest
import pe.ferrestock.app.data.sesion.AlmacenToken
import pe.ferrestock.app.domain.model.Sesion
import retrofit2.HttpException
import java.io.IOException

class AuthRepositoryImpl(
    private val apiService: ApiService
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<Sesion> {
        return try {
            val response = apiService.login(LoginRequest(username, password))
            val sesion = response.toDomain()
            AlmacenToken.token = sesion.token
            Result.success(sesion)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
