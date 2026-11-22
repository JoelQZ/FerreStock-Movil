package pe.ferrestock.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import pe.ferrestock.app.data.local.entity.ProductoEntity

@Dao
interface ProductoDao {
    @Upsert
    suspend fun insertarTodos(productos: List<ProductoEntity>)

    @Query("SELECT * FROM productos")
    fun getProductos(): Flow<List<ProductoEntity>>
}
