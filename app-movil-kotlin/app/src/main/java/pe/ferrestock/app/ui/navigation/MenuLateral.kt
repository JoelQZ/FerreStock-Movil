package pe.ferrestock.app.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import pe.ferrestock.app.domain.model.Sesion
import pe.ferrestock.app.ui.theme.Borde
import pe.ferrestock.app.ui.theme.Carbon
import pe.ferrestock.app.ui.theme.Madera
import pe.ferrestock.app.ui.theme.MaderaOscura
import pe.ferrestock.app.ui.theme.Tarjeta
import pe.ferrestock.app.ui.theme.TextoClaro

@Composable
fun MenuLateral(
    sesion: Sesion?,
    ultimaSincronizacion: String?,
    onPersonal: () -> Unit,
    onSincronizar: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    ModalDrawerSheet(drawerContainerColor = Tarjeta) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(Carbon)
                .statusBarsPadding()
                .padding(20.dp)
        ) {
            Box(
                Modifier
                    .size(52.dp)
                    .background(Madera, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(iniciales(sesion?.usuario), style = MaterialTheme.typography.titleMedium, color = Color.White)
            }
            Spacer(Modifier.height(10.dp))
            Text(sesion?.usuario.orEmpty(), style = MaterialTheme.typography.titleMedium, color = Color.White)
            Text(
                sesion?.rol?.name?.lowercase()?.replaceFirstChar { it.uppercase() }.orEmpty(),
                style = MaterialTheme.typography.labelMedium,
                color = TextoClaro
            )
        }
        Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Opcion(Icons.Outlined.Groups, "Personal", "Cajeros y último acceso", onPersonal)
            HorizontalDivider(color = Borde)
            Opcion(
                Icons.Outlined.Sync,
                "Sincronizar",
                ultimaSincronizacion?.let { "Última vez: $it" } ?: "Guardar datos para usar sin internet",
                onSincronizar
            )
            HorizontalDivider(color = Borde)
        }
        Spacer(Modifier.weight(1f))
        OutlinedButton(
            onClick = onCerrarSesion,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null)
            Text("CERRAR SESIÓN", modifier = Modifier.padding(start = 6.dp))
        }
    }
}

@Composable
private fun Opcion(icono: ImageVector, titulo: String, detalle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icono, contentDescription = null, tint = MaderaOscura)
        Column(
            Modifier
                .weight(1f)
                .padding(start = 14.dp)
        ) {
            Text(titulo, style = MaterialTheme.typography.bodyLarge)
            Text(detalle, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun iniciales(nombre: String?): String =
    nombre.orEmpty().split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }
