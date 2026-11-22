package pe.ferrestock.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import pe.ferrestock.app.data.local.entity.PrediccionEntity

@Dao
interface PrediccionDao {
    @Upsert
    suspend fun insertarTodas(predicciones: List<PrediccionEntity>)

    @Query("SELECT * FROM predicciones")
    fun getPredicciones(): Flow<List<PrediccionEntity>>
}
