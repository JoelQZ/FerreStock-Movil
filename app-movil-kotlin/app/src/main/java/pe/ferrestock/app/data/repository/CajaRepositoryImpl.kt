package pe.ferrestock.app.data.repository

import kotlinx.serialization.json.Json
import pe.ferrestock.app.data.mapper.aVista
import pe.ferrestock.app.data.remote.ApiService
import pe.ferrestock.app.data.remote.dto.TurnoDto
import pe.ferrestock.app.data.remote.dto.VentaDto
import pe.ferrestock.app.data.remote.dto.parseList
import pe.ferrestock.app.domain.model.TurnoVista
import pe.ferrestock.app.domain.model.VentaVista
import retrofit2.HttpException
import java.io.IOException

class CajaRepositoryImpl(
    private val apiService: ApiService,
    private val json: Json = Json { ignoreUnknownKeys = true; coerceInputValues = true }
) : CajaRepository {

    override suspend fun getTurnos(): Result<List<TurnoVista>> {
        return try {
            val jsonElement = apiService.getTurnos()
            val dtos = jsonElement.parseList(json, TurnoDto.serializer())
            Result.success(dtos.map { it.aVista() })
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getResumenTurno(id: Int): Result<Double> {
        return try {
            val dto = apiService.getResumenTurno(id)
            val monto = dto.montoEsperado.toDoubleOrNull() ?: 0.0
            Result.success(monto)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getVentasAnuladas(): Result<List<VentaVista>> {
        return try {
            val jsonElement = apiService.getVentas()
            val dtos = jsonElement.parseList(json, VentaDto.serializer())
            val ventasVista = dtos.map { it.aVista() }
            val anuladas = ventasVista.filter { it.estado.equals("ANULADA", ignoreCase = true) }
            Result.success(anuladas)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
