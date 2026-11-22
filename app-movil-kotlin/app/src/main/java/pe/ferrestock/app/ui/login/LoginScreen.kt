package pe.ferrestock.app.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pe.ferrestock.app.domain.model.Sesion
import pe.ferrestock.app.ui.UiState
import pe.ferrestock.app.ui.components.coloresCampo
import pe.ferrestock.app.ui.theme.Carbon
import pe.ferrestock.app.ui.theme.Madera
import pe.ferrestock.app.ui.theme.TextoClaro

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginExitoso: (Sesion) -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    var usuario by rememberSaveable { mutableStateOf("") }
    var clave by rememberSaveable { mutableStateOf("") }
    var enviado by rememberSaveable { mutableStateOf(false) }
    var errorCampos by rememberSaveable { mutableStateOf<String?>(null) }
    var estadoAnterior by remember { mutableStateOf<UiState<Sesion>?>(null) }
    val respondio = enviado && estado !== estadoAnterior
    val cargando = enviado && (!respondio || estado is UiState.Cargando)
    val error = errorCampos ?: if (respondio) (estado as? UiState.Error)?.mensaje else null

    LaunchedEffect(estado, enviado) {
        val sesion = (estado as? UiState.Exito<Sesion>)?.datos
        if (enviado && estado !== estadoAnterior && sesion != null) {
            enviado = false
            onLoginExitoso(sesion)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Carbon)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("FERRESTOCK", style = MaterialTheme.typography.headlineSmall, color = Madera)
        Text("Gestión para administradores", style = MaterialTheme.typography.bodyMedium, color = TextoClaro)
        Spacer(Modifier.height(24.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = usuario,
                    onValueChange = { usuario = it },
                    label = { Text("Usuario") },
                    singleLine = true,
                    colors = coloresCampo(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = clave,
                    onValueChange = { clave = it },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = coloresCampo(),
                    modifier = Modifier.fillMaxWidth()
                )
                if (error != null) {
                    Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                }
                Button(
                    onClick = {
                        if (usuario.isBlank() || clave.isBlank()) {
                            errorCampos = "Completa el usuario y la contraseña"
                        } else {
                            errorCampos = null
                            estadoAnterior = estado
                            enviado = true
                            viewModel.login(usuario.trim(), clave)
                        }
                    },
                    enabled = !cargando,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (cargando) {
                        CircularProgressIndicator(Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("INGRESAR")
                    }
                }
            }
        }
    }
}
