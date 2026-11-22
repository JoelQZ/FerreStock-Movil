package pe.ferrestock.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sincronizacion")
data class SincronizacionEntity(
    @PrimaryKey val id: Int = 1,
    val fechaHora: String
)
