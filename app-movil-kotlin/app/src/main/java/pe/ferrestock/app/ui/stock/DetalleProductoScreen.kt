package pe.ferrestock.app.ui.stock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.ferrestock.app.domain.model.MovimientoVista
import pe.ferrestock.app.ui.UiState
import pe.ferrestock.app.ui.components.ContenidoSegunEstado
import pe.ferrestock.app.ui.components.Encabezado
import pe.ferrestock.app.ui.components.Etiqueta
import pe.ferrestock.app.ui.components.EtiquetaStock
import pe.ferrestock.app.ui.components.Kpi
import pe.ferrestock.app.ui.components.Tarjeta
import pe.ferrestock.app.ui.components.TituloSeccion
import pe.ferrestock.app.ui.components.TonoEtiqueta
import pe.ferrestock.app.ui.components.Vacio
import pe.ferrestock.app.ui.components.cantidad
import pe.ferrestock.app.ui.components.soles
import pe.ferrestock.app.ui.theme.AgotadoTexto
import pe.ferrestock.app.ui.theme.BajoTexto
import pe.ferrestock.app.ui.theme.Borde
import pe.ferrestock.app.ui.theme.Verde

@Composable
fun DetalleProductoScreen(
    estado: UiState<DetalleEstado>,
    onVolver: () -> Unit,
    onReintentar: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        val producto = (estado as? UiState.Exito)?.datos?.producto
        Encabezado(producto?.nombre ?: "Producto", producto?.codigo, onVolver = onVolver)
        ContenidoSegunEstado(estado, onReintentar) { datos ->
            val p = datos.producto
            if (p == null) {
                Vacio("No se encontró el producto", "Sincroniza el stock e inténtalo otra vez")
            } else LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Tarjeta {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("Código ${p.codigo}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(soles(p.precio), style = MaterialTheme.typography.headlineSmall)
                                Text("Precio de venta", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            EtiquetaStock(p)
                        }
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Kpi("Stock actual", cantidad(p.stock), Modifier.weight(1f))
                        Kpi("Stock mínimo", cantidad(p.stockMinimo), Modifier.weight(1f))
                    }
                }
                item {
                    val color = when {
                        p.stock <= 0 -> AgotadoTexto
                        p.stock <= p.stockMinimo -> BajoTexto
                        else -> Verde
                    }
                    val proporcion = if (p.stockMinimo <= 0) 1f else (p.stock.toFloat() / (p.stockMinimo * 2)).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { proporcion },
                        modifier = Modifier.fillMaxWidth(),
                        color = color,
                        trackColor = Borde,
                        drawStopIndicator = {}
                    )
                }
                item { TituloSeccion("Kárdex") }
                if (datos.movimientos.isEmpty()) {
                    item {
                        Text(
                            "Este producto todavía no tiene movimientos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                items(datos.movimientos) { movimiento -> FilaMovimiento(movimiento) }
            }
        }
    }
}

@Composable
private fun FilaMovimiento(movimiento: MovimientoVista) {
    val (tono, signo) = when (movimiento.tipo.uppercase()) {
        "ENTRADA" -> TonoEtiqueta.OK to "+"
        "SALIDA" -> TonoEtiqueta.AGOTADO to "-"
        else -> TonoEtiqueta.BAJO to "±"
    }
    Tarjeta {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Etiqueta(movimiento.tipo.lowercase().replaceFirstChar { it.uppercase() }, tono)
                Text(
                    "${movimiento.fecha} · ${movimiento.usuarioNombre}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("$signo${cantidad(movimiento.cantidad)}", style = MaterialTheme.typography.titleMedium, color = tono.texto)
                Text(
                    "${cantidad(movimiento.stockAnterior)} → ${cantidad(movimiento.stockNuevo)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
