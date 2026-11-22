package pe.ferrestock.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import pe.ferrestock.app.data.local.dao.PrediccionDao
import pe.ferrestock.app.data.local.dao.ProductoDao
import pe.ferrestock.app.data.local.dao.SincronizacionDao
import pe.ferrestock.app.data.local.entity.PrediccionEntity
import pe.ferrestock.app.data.local.entity.ProductoEntity
import pe.ferrestock.app.data.local.entity.SincronizacionEntity

@Database(
    entities = [
        ProductoEntity::class,
        PrediccionEntity::class,
        SincronizacionEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productoDao(): ProductoDao
    abstract fun prediccionDao(): PrediccionDao
    abstract fun sincronizacionDao(): SincronizacionDao
}
