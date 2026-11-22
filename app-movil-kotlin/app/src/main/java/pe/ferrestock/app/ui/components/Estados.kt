package pe.ferrestock.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pe.ferrestock.app.ui.UiState
import pe.ferrestock.app.ui.theme.Madera

@Composable
fun Cargando() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Madera)
    }
}

@Composable
fun Mensaje(icono: ImageVector, titulo: String, detalle: String? = null, accion: String? = null, onAccion: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(40.dp))
        Text(titulo, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        if (detalle != null) {
            Text(detalle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
        if (accion != null) {
            OutlinedButton(onClick = onAccion) { Text(accion) }
        }
    }
}

@Composable
fun Vacio(titulo: String, detalle: String? = null) = Mensaje(Icons.Outlined.Inbox, titulo, detalle)

@Composable
fun <T> ContenidoSegunEstado(
    estado: UiState<T>,
    onReintentar: () -> Unit,
    contenido: @Composable (T) -> Unit
) {
    when (estado) {
        UiState.Cargando -> Cargando()
        is UiState.Error -> Mensaje(Icons.Outlined.CloudOff, estado.mensaje, accion = "Reintentar", onAccion = onReintentar)
        is UiState.Exito -> contenido(estado.datos)
    }
}
