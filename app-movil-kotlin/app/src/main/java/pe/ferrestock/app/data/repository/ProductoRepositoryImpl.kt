package pe.ferrestock.app.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pe.ferrestock.app.data.local.dao.ProductoDao
import pe.ferrestock.app.data.local.dao.SincronizacionDao
import pe.ferrestock.app.data.local.entity.SincronizacionEntity
import pe.ferrestock.app.data.mapper.formatearFecha
import pe.ferrestock.app.data.mapper.toDomain
import pe.ferrestock.app.data.mapper.toEntity
import pe.ferrestock.app.data.remote.ApiService
import pe.ferrestock.app.domain.model.Producto
import retrofit2.HttpException
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ProductoRepositoryImpl(
    private val apiService: ApiService,
    private val productoDao: ProductoDao,
    private val sincronizacionDao: SincronizacionDao
) : ProductoRepository {

    override fun getProductos(): Flow<List<Producto>> {
        return productoDao.getProductos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun refrescar(): Result<Unit> {
        return try {
            val dtos = apiService.getProductos()
            val entities = dtos.map { it.toEntity() }
            productoDao.insertarTodos(entities)
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

    override fun getUltimaSincronizacion(): Flow<String?> {
        return sincronizacionDao.getSincronizacion().map { it?.fechaHora }
    }
}
