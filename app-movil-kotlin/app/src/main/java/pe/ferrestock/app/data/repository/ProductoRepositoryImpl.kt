package pe.ferrestock.app.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pe.ferrestock.app.data.local.dao.ProductoDao
import pe.ferrestock.app.data.mapper.toDomain
import pe.ferrestock.app.data.mapper.toEntity
import pe.ferrestock.app.data.remote.ApiService
import pe.ferrestock.app.domain.model.Producto
import retrofit2.HttpException
import java.io.IOException

class ProductoRepositoryImpl(
    private val apiService: ApiService,
    private val productoDao: ProductoDao
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
            Result.success(Unit)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
