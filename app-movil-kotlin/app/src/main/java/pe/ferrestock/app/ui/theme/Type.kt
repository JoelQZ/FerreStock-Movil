package pe.ferrestock.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val FuenteTitulo = FontFamily.SansSerif
val Fuente = FontFamily.SansSerif

val Typography = Typography(
    headlineSmall = TextStyle(fontFamily = FuenteTitulo, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, letterSpacing = 0.5.sp),
    titleLarge = TextStyle(fontFamily = FuenteTitulo, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, letterSpacing = 0.5.sp),
    titleMedium = TextStyle(fontFamily = FuenteTitulo, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, letterSpacing = 0.5.sp),
    bodyLarge = TextStyle(fontFamily = Fuente, fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontFamily = Fuente, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontFamily = Fuente, fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
    labelMedium = TextStyle(fontFamily = Fuente, fontWeight = FontWeight.Medium, fontSize = 12.sp)
)
