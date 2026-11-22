package pe.ferrestock.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pe.ferrestock.app.domain.model.Producto
import pe.ferrestock.app.ui.theme.AgotadoFondo
import pe.ferrestock.app.ui.theme.AgotadoTexto
import pe.ferrestock.app.ui.theme.BajoFondo
import pe.ferrestock.app.ui.theme.BajoTexto
import pe.ferrestock.app.ui.theme.Verde
import pe.ferrestock.app.ui.theme.VerdeFondo

enum class TonoEtiqueta(val fondo: Color, val texto: Color) {
    OK(VerdeFondo, Verde),
    BAJO(BajoFondo, BajoTexto),
    AGOTADO(AgotadoFondo, AgotadoTexto)
}

@Composable
fun Etiqueta(texto: String, tono: TonoEtiqueta, modifier: Modifier = Modifier) {
    Text(
        texto,
        style = MaterialTheme.typography.labelMedium,
        color = tono.texto,
        modifier = modifier
            .background(tono.fondo, MaterialTheme.shapes.small)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

@Composable
fun EtiquetaStock(producto: Producto) {
    when {
        producto.stock <= 0 -> Etiqueta("Agotado", TonoEtiqueta.AGOTADO)
        producto.stock <= producto.stockMinimo -> Etiqueta("Stock bajo", TonoEtiqueta.BAJO)
        else -> Etiqueta("Disponible", TonoEtiqueta.OK)
    }
}
