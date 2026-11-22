package pe.ferrestock.app.data.repository

import kotlinx.serialization.json.Json
import pe.ferrestock.app.data.mapper.aVista
import pe.ferrestock.app.data.remote.ApiService
import pe.ferrestock.app.data.remote.dto.ProductoMasVendidoDto
import pe.ferrestock.app.data.remote.dto.parseList
import pe.ferrestock.app.domain.model.ProductoMasVendidoVista
import pe.ferrestock.app.domain.model.ResumenVentaVista
import retrofit2.HttpException
import java.io.IOException

class ReporteRepositoryImpl(
    private val apiService: ApiService,
    private val json: Json = Json { ignoreUnknownKeys = true; coerceInputValues = true }
) : ReporteRepository {

    override suspend fun getResumenVentas(desde: String, hasta: String): Result<ResumenVentaVista> {
        return try {
            val dto = apiService.getReporteVentas(desde, hasta)
            Result.success(dto.aVista())
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProductosMasVendidos(desde: String, hasta: String, limite: Int): Result<List<ProductoMasVendidoVista>> {
        return try {
            val jsonElement = apiService.getProductosMasVendidos(desde, hasta, limite)
            val dtos = jsonElement.parseList(json, ProductoMasVendidoDto.serializer())
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
