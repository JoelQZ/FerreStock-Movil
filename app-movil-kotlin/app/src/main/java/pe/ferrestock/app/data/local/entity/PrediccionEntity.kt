package pe.ferrestock.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "predicciones")
data class PrediccionEntity(
    @PrimaryKey val productoId: Int,
    val productoCodigo: String,
    val productoNombre: String,
    val stockActual: Int,
    val stockMinimo: Int,
    val demandaEstimada: Int,
    val diasParaAgotarse: Int,
    val cantidadSugerida: Int
)
