package pe.ferrestock.app.ui.caja

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.ferrestock.app.domain.model.TurnoVista
import pe.ferrestock.app.domain.model.VentaVista
import pe.ferrestock.app.ui.UiState
import pe.ferrestock.app.ui.components.ChipsFiltro
import pe.ferrestock.app.ui.components.ContenidoSegunEstado
import pe.ferrestock.app.ui.components.Encabezado
import pe.ferrestock.app.ui.components.Etiqueta
import pe.ferrestock.app.ui.components.Tarjeta
import pe.ferrestock.app.ui.components.TonoEtiqueta
import pe.ferrestock.app.ui.components.Vacio
import pe.ferrestock.app.ui.components.soles

private enum class VistaCaja(val texto: String) {
    ABIERTOS("Abiertos"),
    CIERRES("Cierres"),
    ANULADAS("Anuladas")
}

@Composable
fun CajaScreen(estado: UiState<CajaEstado>, onReintentar: () -> Unit) {
    var vista by rememberSaveable { mutableStateOf(VistaCaja.ABIERTOS) }

    Column(Modifier.fillMaxSize()) {
        Encabezado("Caja", "Turnos y anulaciones")
        ChipsFiltro(
            opciones = VistaCaja.entries,
            seleccion = vista,
            texto = { it.texto },
            onSeleccionar = { vista = it },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        ContenidoSegunEstado(estado, onReintentar) { datos ->
            when (vista) {
                VistaCaja.ABIERTOS -> ListaTurnos(datos.turnosAbiertos, "No hay turnos abiertos")
                VistaCaja.CIERRES -> ListaTurnos(datos.turnosCerrados, "Todavía no hay cierres")
                VistaCaja.ANULADAS -> ListaAnuladas(datos.ventasAnuladas)
            }
        }
    }
}

@Composable
private fun ListaTurnos(turnos: List<TurnoVista>, textoVacio: String) {
    if (turnos.isEmpty()) {
        Vacio(textoVacio)
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(turnos, key = { it.idTurno }) { turno -> TarjetaTurno(turno) }
    }
}

@Composable
private fun TarjetaTurno(turno: TurnoVista) {
    val abierto = turno.cierre.isNullOrBlank()
    val descuadrado = (turno.diferencia ?: 0.0) != 0.0
    Tarjeta {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(turno.usuarioNombre, style = MaterialTheme.typography.titleMedium)
                Text(
                    "Turno #${turno.idTurno} · ${turno.apertura}${turno.cierre?.takeIf { it.isNotBlank() }?.let { " a $it" } ?: ""}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            when {
                abierto -> Etiqueta("Abierto", TonoEtiqueta.OK)
                descuadrado -> Etiqueta("Descuadre", TonoEtiqueta.AGOTADO)
                else -> Etiqueta("Cuadrado", TonoEtiqueta.OK)
            }
        }
        Row(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Dato("Inicial", soles(turno.montoInicial))
            turno.montoEsperado?.let { Dato("Esperado", soles(it)) }
            turno.montoReal?.let { Dato("Contado", soles(it)) }
            turno.diferencia?.let { Dato("Diferencia", soles(it)) }
        }
    }
}

@Composable
private fun ListaAnuladas(ventas: List<VentaVista>) {
    if (ventas.isEmpty()) {
        Vacio("No hay ventas anuladas")
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(ventas, key = { it.idVenta }) { venta ->
            Tarjeta {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Venta #${venta.idVenta}", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "${venta.fechaHora} · ${venta.usuarioNombre}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(soles(venta.total), style = MaterialTheme.typography.labelLarge)
                        Etiqueta("Anulada", TonoEtiqueta.AGOTADO)
                    }
                }
            }
        }
    }
}

@Composable
private fun Dato(etiqueta: String, valor: String) {
    Column {
        Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valor, style = MaterialTheme.typography.labelLarge)
    }
}
