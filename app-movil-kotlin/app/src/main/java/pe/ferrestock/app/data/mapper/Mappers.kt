package pe.ferrestock.app.data.mapper

import pe.ferrestock.app.data.local.entity.ProductoEntity
import pe.ferrestock.app.data.remote.dto.LoginResponse
import pe.ferrestock.app.data.remote.dto.MetodoPagoDto
import pe.ferrestock.app.data.remote.dto.MovimientoKardexDto
import pe.ferrestock.app.data.remote.dto.PrediccionDto
import pe.ferrestock.app.data.remote.dto.ProductoDto
import pe.ferrestock.app.data.remote.dto.ProductoMasVendidoDto
import pe.ferrestock.app.data.remote.dto.ProveedorDto
import pe.ferrestock.app.data.remote.dto.ReporteVentasDto
import pe.ferrestock.app.data.remote.dto.TurnoDto
import pe.ferrestock.app.data.remote.dto.UsuarioDto
import pe.ferrestock.app.data.remote.dto.VentaDto
import pe.ferrestock.app.domain.model.MetodoPagoVista
import pe.ferrestock.app.domain.model.MovimientoVista
import pe.ferrestock.app.domain.model.PrediccionVista
import pe.ferrestock.app.domain.model.Producto
import pe.ferrestock.app.domain.model.ProductoMasVendidoVista
import pe.ferrestock.app.domain.model.ProveedorVista
import pe.ferrestock.app.domain.model.ResumenVentaVista
import pe.ferrestock.app.domain.model.Rol
import pe.ferrestock.app.domain.model.Sesion
import pe.ferrestock.app.domain.model.TurnoVista
import pe.ferrestock.app.domain.model.UsuarioVista
import pe.ferrestock.app.domain.model.VentaVista
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun formatearFecha(iso: String?): String {
    if (iso.isNullOrBlank()) return ""
    return try {
        val parsedDateTime = try {
            ZonedDateTime.parse(iso)
        } catch (e: Exception) {
            try {
                LocalDateTime.parse(iso)
            } catch (e2: Exception) {
                try {
                    OffsetDateTime.parse(iso).toZonedDateTime()
                } catch (e3: Exception) {
                    val instant = Instant.parse(iso)
                    instant.atZone(ZoneId.systemDefault())
                }
            }
        }
        val dateTime = when (parsedDateTime) {
            is ZonedDateTime -> parsedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
            is LocalDateTime -> parsedDateTime
            else -> LocalDateTime.now()
        }
        val hoy = LocalDate.now()
        val fecha = dateTime.toLocalDate()
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val horaStr = dateTime.format(timeFormatter)

        when {
            fecha == hoy -> "Hoy $horaStr"
            fecha == hoy.minusDays(1) -> "Ayer $horaStr"
            else -> dateTime.format(DateTimeFormatter.ofPattern("dd/MM HH:mm"))
        }
    } catch (e: Exception) {
        iso
    }
}

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

fun ProductoDto.aVista(): Producto = toDomain()

fun MetodoPagoDto.aVista(): MetodoPagoVista {
    return MetodoPagoVista(
        metodo = metodo,
        cantidadVentas = cantidadVentas,
        total = total.toDoubleOrNull() ?: 0.0
    )
}

fun ReporteVentasDto.aVista(): ResumenVentaVista {
    val cant = cantidadVentas ?: cantVentas ?: resumen?.cantidadVentas ?: resumen?.cantVentas ?: 0
    val totStr = total ?: resumen?.total ?: "0.00"
    return ResumenVentaVista(
        cantidadVentas = cant,
        total = totStr.toDoubleOrNull() ?: 0.0,
        porMetodoPago = porMetodoPago.map { it.aVista() }
    )
}

fun ProductoMasVendidoDto.aVista(): ProductoMasVendidoVista {
    val pid = if (productoId != 0) productoId else (producto ?: 0)
    return ProductoMasVendidoVista(
        productoId = pid,
        codigo = codigo,
        nombre = nombre,
        cantidadVendida = cantidadVendida,
        totalVendido = totalVendido.toDoubleOrNull() ?: 0.0
    )
}

fun TurnoDto.aVista(): TurnoVista {
    val tid = idTurno ?: id ?: 0
    val user = usuarioNombre ?: usuario ?: ""
    return TurnoVista(
        idTurno = tid,
        usuarioNombre = user,
        apertura = formatearFecha(apertura),
        montoInicial = montoInicial.toDoubleOrNull() ?: 0.0,
        cierre = cierre?.let { formatearFecha(it) },
        montoEsperado = montoEsperado?.toDoubleOrNull(),
        montoReal = montoReal?.toDoubleOrNull(),
        diferencia = diferencia?.toDoubleOrNull(),
        estado = estado
    )
}

fun VentaDto.aVista(): VentaVista {
    val vid = idVenta ?: id ?: 0
    val user = usuarioNombre ?: usuario ?: ""
    return VentaVista(
        idVenta = vid,
        fechaHora = formatearFecha(fechaHora ?: fecha),
        total = total.toDoubleOrNull() ?: 0.0,
        estado = estado,
        usuarioNombre = user
    )
}

fun PrediccionDto.aVista(): PrediccionVista {
    val pid = productoId ?: producto ?: 0
    return PrediccionVista(
        productoId = pid,
        productoCodigo = productoCodigo,
        productoNombre = productoNombre,
        stockActual = stockActual,
        stockMinimo = stockMinimo,
        demandaEstimada = demandaEstimada,
        diasParaAgotarse = diasParaAgotarse,
        cantidadSugerida = cantidadSugerida
    )
}

fun MovimientoKardexDto.aVista(): MovimientoVista {
    val pid = productoId ?: producto
    val user = usuarioNombre ?: usuario ?: ""
    return MovimientoVista(
        productoId = pid,
        tipo = tipo,
        cantidad = cantidad,
        stockAnterior = stockAnterior,
        stockNuevo = stockNuevo,
        fecha = formatearFecha(fecha),
        usuarioNombre = user
    )
}

fun ProveedorDto.aVista(): ProveedorVista {
    val pid = idProveedor ?: id ?: 0
    return ProveedorVista(
        idProveedor = pid,
        ruc = ruc,
        razonSocial = razonSocial,
        estado = estado
    )
}

fun UsuarioDto.aVista(): UsuarioVista {
    val user = usuario ?: username ?: ""
    val name = nombre ?: listOfNotNull(firstName, lastName).joinToString(" ").ifBlank { user }
    return UsuarioVista(
        id = id,
        nombre = name,
        usuario = user,
        rolNombre = rolNombre ?: rol ?: "",
        estado = isActive ?: estado,
        lastLogin = lastLogin?.let { formatearFecha(it) }
    )
}
