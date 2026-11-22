package pe.ferrestock.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import pe.ferrestock.app.data.local.dao.ProductoDao
import pe.ferrestock.app.data.local.entity.ProductoEntity

@Database(entities = [ProductoEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productoDao(): ProductoDao
}
