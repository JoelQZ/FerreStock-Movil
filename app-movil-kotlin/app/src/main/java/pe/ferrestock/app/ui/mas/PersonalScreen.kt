package pe.ferrestock.app.ui.mas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.ferrestock.app.domain.model.UsuarioVista
import pe.ferrestock.app.ui.UiState
import pe.ferrestock.app.ui.components.ContenidoSegunEstado
import pe.ferrestock.app.ui.components.Encabezado
import pe.ferrestock.app.ui.components.Etiqueta
import pe.ferrestock.app.ui.components.Tarjeta
import pe.ferrestock.app.ui.components.TonoEtiqueta
import pe.ferrestock.app.ui.components.Vacio

@Composable
fun PersonalScreen(
    estado: UiState<List<UsuarioVista>>,
    onVolver: () -> Unit,
    onReintentar: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        Encabezado("Personal", "Solo lectura", onVolver = onVolver)
        ContenidoSegunEstado(estado, onReintentar) { usuarios ->
            if (usuarios.isEmpty()) {
                Vacio("No hay usuarios registrados")
            } else LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(usuarios, key = { it.id }) { usuario ->
                    Tarjeta {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(usuario.nombre, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "@${usuario.usuario} · ${usuario.rolNombre.lowercase().replaceFirstChar { it.uppercase() }}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    usuario.lastLogin?.let { "Último acceso: $it" } ?: "Nunca ingresó",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (usuario.estado) Etiqueta("Activo", TonoEtiqueta.OK) else Etiqueta("Inactivo", TonoEtiqueta.AGOTADO)
                        }
                    }
                }
            }
        }
    }
}
