package pe.ferrestock.app.ui.mas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import pe.ferrestock.app.domain.model.Sesion
import pe.ferrestock.app.ui.components.Encabezado
import pe.ferrestock.app.ui.components.Tarjeta
import pe.ferrestock.app.ui.components.TituloSeccion
import pe.ferrestock.app.ui.theme.Borde
import pe.ferrestock.app.ui.theme.Madera
import pe.ferrestock.app.ui.theme.MaderaOscura

@Composable
fun MasScreen(
    sesion: Sesion?,
    ultimaSincronizacion: String?,
    onPersonal: () -> Unit,
    onSincronizar: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        Encabezado("Más", "Perfil y ajustes")
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Tarjeta {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(48.dp)
                            .background(Madera, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(iniciales(sesion?.usuario), style = MaterialTheme.typography.titleMedium, color = Color.White)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(sesion?.usuario ?: "", style = MaterialTheme.typography.titleMedium)
                        Text(
                            sesion?.rol?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            TituloSeccion("Gestión")
            Tarjeta {
                Opcion(Icons.Outlined.Groups, "Personal", "Cajeros y último acceso", onPersonal)
                HorizontalDivider(Modifier.padding(vertical = 4.dp), color = Borde)
                Opcion(Icons.Outlined.Sync, "Sincronizar", ultimaSincronizacion?.let { "Última vez: $it" } ?: "Guardar datos para usar sin internet", onSincronizar)
            }
            OutlinedButton(onClick = onCerrarSesion, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null)
                Text("CERRAR SESIÓN", modifier = Modifier.padding(start = 6.dp))
            }
        }
    }
}

@Composable
private fun Opcion(icono: ImageVector, titulo: String, detalle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icono, contentDescription = null, tint = MaderaOscura)
        Column(
            Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(titulo, style = MaterialTheme.typography.bodyLarge)
            Text(detalle, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun iniciales(nombre: String?): String =
    nombre.orEmpty().split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }
