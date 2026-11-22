package pe.ferrestock.app.data.mapper

import pe.ferrestock.app.data.local.entity.ProductoEntity
import pe.ferrestock.app.data.remote.dto.LoginResponse
import pe.ferrestock.app.data.remote.dto.ProductoDto
import pe.ferrestock.app.domain.model.Producto
import pe.ferrestock.app.domain.model.Rol
import pe.ferrestock.app.domain.model.Sesion

fun LoginResponse.toDomain(): Sesion {
    val userStr = usuario ?: username ?: ""
    val rolStr = rol ?: rolNombre ?: ""
    return Sesion(
        token = access,
        usuario = userStr,
        rol = Rol.fromString(rolStr),
        firebaseToken = firebaseToken
    )
}

fun ProductoDto.toEntity(): ProductoEntity {
    return ProductoEntity(
        id = id,
        codigo = codigo,
        nombre = nombre,
        precio = precio.toDoubleOrNull() ?: 0.0,
        stock = stock,
        stockMinimo = stockMinimo
    )
}

fun ProductoEntity.toDomain(): Producto {
    return Producto(
        id = id,
        codigo = codigo,
        nombre = nombre,
        precio = precio,
        stock = stock,
        stockMinimo = stockMinimo
    )
}

fun ProductoDto.toDomain(): Producto {
    return Producto(
        id = id,
        codigo = codigo,
        nombre = nombre,
        precio = precio.toDoubleOrNull() ?: 0.0,
        stock = stock,
        stockMinimo = stockMinimo
    )
}
