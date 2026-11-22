package pe.ferrestock.app.domain.model

data class Producto(
    val id: Int,
    val codigo: String,
    val nombre: String,
    val precio: Double,
    val stock: Int,
    val stockMinimo: Int
)
