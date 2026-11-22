package pe.ferrestock.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import pe.ferrestock.app.data.local.entity.SincronizacionEntity

@Dao
interface SincronizacionDao {
    @Upsert
    suspend fun guardar(sincronizacion: SincronizacionEntity)

    @Query("SELECT * FROM sincronizacion WHERE id = 1")
    fun getSincronizacion(): Flow<SincronizacionEntity?>
}
