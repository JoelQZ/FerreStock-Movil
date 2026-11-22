package pe.ferrestock.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey val id: Int,
    val codigo: String,
    val nombre: String,
    val precio: Double,
    val stock: Int,
    val stockMinimo: Int
)
