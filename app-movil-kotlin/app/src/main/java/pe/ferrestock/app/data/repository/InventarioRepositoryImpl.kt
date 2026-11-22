package pe.ferrestock.app.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import pe.ferrestock.app.data.local.dao.PrediccionDao
import pe.ferrestock.app.data.local.dao.SincronizacionDao
import pe.ferrestock.app.data.local.entity.SincronizacionEntity
import pe.ferrestock.app.data.mapper.aVista
import pe.ferrestock.app.data.mapper.formatearFecha
import pe.ferrestock.app.data.mapper.toEntity
import pe.ferrestock.app.data.remote.ApiService
import pe.ferrestock.app.data.remote.dto.DetalleIngresoDto
import pe.ferrestock.app.data.remote.dto.IngresoRequestDto
import pe.ferrestock.app.data.remote.dto.IngresoResponseDto
import pe.ferrestock.app.data.remote.dto.MovimientoKardexDto
import pe.ferrestock.app.data.remote.dto.PrediccionDto
import pe.ferrestock.app.data.remote.dto.ProveedorDto
import pe.ferrestock.app.data.remote.dto.parseList
import pe.ferrestock.app.domain.model.MovimientoVista
import pe.ferrestock.app.domain.model.PrediccionVista
import pe.ferrestock.app.domain.model.ProveedorVista
import retrofit2.HttpException
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class InventarioRepositoryImpl(
    private val apiService: ApiService,
    private val prediccionDao: PrediccionDao,
    private val sincronizacionDao: SincronizacionDao,
    private val json: Json = Json { ignoreUnknownKeys = true; coerceInputValues = true }
) : InventarioRepository {

    override fun getPredicciones(): Flow<List<PrediccionVista>> {
        return prediccionDao.getPredicciones().map { entities ->
            entities.map { it.aVista() }
        }
    }

    override suspend fun refrescarPredicciones(): Result<Unit> {
        return try {
            val jsonElement = apiService.getPredicciones()
            val dtos = jsonElement.parseList(json, PrediccionDto.serializer())
            val entities = dtos.map { it.toEntity() }
            prediccionDao.insertarTodas(entities)
            val ahoraIso = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            sincronizacionDao.guardar(SincronizacionEntity(id = 1, fechaHora = formatearFecha(ahoraIso)))
            Result.success(Unit)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getKardex(): Result<List<MovimientoVista>> {
        return try {
            val jsonElement = apiService.getKardex()
            val dtos = jsonElement.parseList(json, MovimientoKardexDto.serializer())
            Result.success(dtos.map { it.aVista() })
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProveedores(): Result<List<ProveedorVista>> {
        return try {
            val jsonElement = apiService.getProveedores()
            val dtos = jsonElement.parseList(json, ProveedorDto.serializer())
            Result.success(dtos.map { it.aVista() })
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registrarIngreso(
        proveedorId: Int,
        nroDocumento: String,
        detalles: List<DetalleIngresoDto>
    ): Result<IngresoResponseDto> {
        return try {
            val request = IngresoRequestDto(proveedorId, nroDocumento, detalles)
            val response = apiService.registrarIngreso(request)
            Result.success(response)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
