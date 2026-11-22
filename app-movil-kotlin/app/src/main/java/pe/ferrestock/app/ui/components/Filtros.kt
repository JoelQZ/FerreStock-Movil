package pe.ferrestock.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import pe.ferrestock.app.ui.theme.Borde
import pe.ferrestock.app.ui.theme.Madera
import pe.ferrestock.app.ui.theme.MaderaOscura
import pe.ferrestock.app.ui.theme.Tarjeta

@Composable
fun <T> ChipsFiltro(
    opciones: List<T>,
    seleccion: T,
    texto: (T) -> String,
    onSeleccionar: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        opciones.forEach { opcion ->
            FilterChip(
                selected = opcion == seleccion,
                onClick = { onSeleccionar(opcion) },
                label = { Text(texto(opcion)) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Tarjeta,
                    selectedContainerColor = Madera,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
fun Buscador(texto: String, onTexto: (String) -> Unit, modifier: Modifier = Modifier, pista: String = "Buscar producto o código") {
    OutlinedTextField(
        value = texto,
        onValueChange = onTexto,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(pista) },
        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
        trailingIcon = {
            if (texto.isNotEmpty()) {
                IconButton(onClick = { onTexto("") }) {
                    Icon(Icons.Outlined.Close, contentDescription = "Limpiar")
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        colors = coloresCampo()
    )
}

@Composable
fun coloresCampo() = OutlinedTextFieldDefaults.colors(
    unfocusedContainerColor = Tarjeta,
    focusedContainerColor = Tarjeta,
    unfocusedBorderColor = Borde,
    focusedBorderColor = Madera,
    focusedLabelColor = MaderaOscura,
    cursorColor = Madera
)
