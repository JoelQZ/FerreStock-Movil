package pe.ferrestock.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val ColoresFerreStock = lightColorScheme(
    primary = Madera,
    onPrimary = Color.White,
    primaryContainer = BajoFondo,
    onPrimaryContainer = MaderaOscura,
    secondary = Acero,
    onSecondary = Color.White,
    secondaryContainer = TarjetaSuave,
    onSecondaryContainer = Carbon,
    tertiary = Verde,
    onTertiary = Color.White,
    tertiaryContainer = VerdeFondo,
    onTertiaryContainer = Verde,
    background = Fondo,
    onBackground = Texto,
    surface = Tarjeta,
    onSurface = Texto,
    surfaceVariant = TarjetaSuave,
    onSurfaceVariant = TextoSuave,
    surfaceContainer = Tarjeta,
    surfaceContainerLow = Tarjeta,
    surfaceContainerHigh = Tarjeta,
    surfaceContainerHighest = TarjetaSuave,
    inverseSurface = Carbon,
    inverseOnSurface = TextoClaro,
    outline = Borde,
    outlineVariant = Borde,
    error = AgotadoTexto,
    onError = Color.White,
    errorContainer = AgotadoFondo,
    onErrorContainer = AgotadoTexto
)

private val Formas = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(6.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(8.dp),
    extraLarge = RoundedCornerShape(12.dp)
)

@Composable
fun FerreStockTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColoresFerreStock,
        typography = Typography,
        shapes = Formas,
        content = content
    )
}
