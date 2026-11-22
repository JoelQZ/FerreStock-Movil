package pe.ferrestock.app.data.repository

import kotlinx.serialization.json.Json
import pe.ferrestock.app.data.mapper.aVista
import pe.ferrestock.app.data.remote.ApiService
import pe.ferrestock.app.data.remote.dto.UsuarioDto
import pe.ferrestock.app.data.remote.dto.parseList
import pe.ferrestock.app.domain.model.UsuarioVista
import retrofit2.HttpException
import java.io.IOException

class UsuarioRepositoryImpl(
    private val apiService: ApiService,
    private val json: Json = Json { ignoreUnknownKeys = true; coerceInputValues = true }
) : UsuarioRepository {

    override suspend fun getUsuarios(): Result<List<UsuarioVista>> {
        return try {
            val jsonElement = apiService.getUsuarios()
            val dtos = jsonElement.parseList(json, UsuarioDto.serializer())
            Result.success(dtos.map { it.aVista() })
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
