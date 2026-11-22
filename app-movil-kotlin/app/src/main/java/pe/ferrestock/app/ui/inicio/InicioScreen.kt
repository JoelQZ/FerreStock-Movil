package pe.ferrestock.app.ui.inicio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import pe.ferrestock.app.domain.model.ProductoMasVendidoVista
import pe.ferrestock.app.ui.UiState
import pe.ferrestock.app.ui.components.ChipsFiltro
import pe.ferrestock.app.ui.components.ContenidoSegunEstado
import pe.ferrestock.app.ui.components.Encabezado
import pe.ferrestock.app.ui.components.Kpi
import pe.ferrestock.app.ui.components.Tarjeta
import pe.ferrestock.app.ui.components.TituloSeccion
import pe.ferrestock.app.ui.components.cantidad
import pe.ferrestock.app.ui.components.soles
import pe.ferrestock.app.ui.theme.AgotadoFondo
import pe.ferrestock.app.ui.theme.AgotadoTexto
import pe.ferrestock.app.ui.theme.BajoFondo
import pe.ferrestock.app.ui.theme.BajoTexto
import pe.ferrestock.app.ui.theme.Borde
import pe.ferrestock.app.ui.theme.Madera

private data class Alerta(val texto: String, val grave: Boolean)

private val Periodo.texto: String
    get() = when (this) {
        Periodo.HOY -> "Hoy"
        Periodo.SEMANA -> "Semana"
        Periodo.MES -> "Mes"
    }

@Composable
fun InicioScreen(
    nombre: String,
    estado: UiState<InicioEstado>,
    periodo: Periodo,
    onPeriodo: (Periodo) -> Unit,
    onReintentar: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        Encabezado("Inicio", "Hola, $nombre") {
            IconButton(onClick = onReintentar) {
                Icon(Icons.Outlined.Refresh, contentDescription = "Actualizar", tint = Color.White)
            }
        }
        ChipsFiltro(
            opciones = Periodo.entries,
            seleccion = periodo,
            texto = { it.texto },
            onSeleccionar = onPeriodo,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        ContenidoSegunEstado(estado, onReintentar) { datos ->
            val total = datos.resumen?.total ?: 0.0
            val ventas = datos.resumen?.cantidadVentas ?: 0
            val ticket = if (ventas == 0) 0.0 else total / ventas
            val alertas = alertasDe(datos)
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Kpi(
                        etiqueta = "Vendido · ${periodo.texto.lowercase()}",
                        valor = soles(total),
                        detalle = "$ventas ventas · ticket promedio ${soles(ticket)}"
                    )
                }
                val metodos = datos.resumen?.porMetodoPago.orEmpty()
                if (metodos.isNotEmpty()) {
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            metodos.forEach { metodo ->
                                Kpi(
                                    etiqueta = metodo.metodo,
                                    valor = soles(metodo.total),
                                    detalle = "${metodo.cantidadVentas} ventas",
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
                if (alertas.isNotEmpty()) {
                    item { TituloSeccion("Alertas") }
                    items(alertas) { alerta -> FilaAlerta(alerta) }
                }
                item { TituloSeccion("Más vendidos") }
                item {
                    if (datos.masVendidos.isEmpty()) {
                        Tarjeta {
                            Text(
                                "Sin ventas en este periodo",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Tarjeta {
                            val maximo = datos.masVendidos.maxOf { it.totalVendido }
                            datos.masVendidos.forEachIndexed { indice, producto ->
                                if (indice > 0) HorizontalDivider(Modifier.padding(vertical = 8.dp), color = Borde)
                                FilaMasVendido(indice + 1, producto, if (maximo == 0.0) 0f else (producto.totalVendido / maximo).toFloat())
                            }
                        }
                    }
                }
                datos.ultimaSincronizacion?.let { fecha ->
                    item {
                        Text(
                            "Última sincronización: $fecha",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private fun alertasDe(datos: InicioEstado): List<Alerta> = buildList {
    val descuadres = datos.turnosConDiferencia.size
    if (descuadres > 0) {
        add(Alerta(if (descuadres == 1) "1 turno cerró con descuadre" else "$descuadres turnos cerraron con descuadre", grave = true))
    }
    val bajos = datos.productosStockBajoCount
    if (bajos > 0) {
        add(Alerta(if (bajos == 1) "1 producto con stock bajo" else "$bajos productos con stock bajo", grave = false))
    }
}

@Composable
private fun FilaAlerta(alerta: Alerta) {
    val fondo = if (alerta.grave) AgotadoFondo else BajoFondo
    val texto = if (alerta.grave) AgotadoTexto else BajoTexto
    Tarjeta(fondo = fondo, borde = fondo) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (alerta.grave) Icons.Outlined.ErrorOutline else Icons.Outlined.WarningAmber,
                contentDescription = null,
                tint = texto,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(alerta.texto, color = texto, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun FilaMasVendido(posicion: Int, producto: ProductoMasVendidoVista, proporcion: Float) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("$posicion", style = MaterialTheme.typography.titleMedium, color = Madera, modifier = Modifier.width(22.dp))
            Column(Modifier.weight(1f)) {
                Text(producto.nombre, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    "${cantidad(producto.cantidadVendida)} vendidos",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(soles(producto.totalVendido), style = MaterialTheme.typography.labelLarge)
        }
        LinearProgressIndicator(
            progress = { proporcion },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 22.dp, top = 6.dp),
            color = Madera,
            trackColor = Borde,
            drawStopIndicator = {}
        )
    }
}
