package pe.ferrestock.app.ui.components

import java.util.Locale

fun soles(monto: Double): String = String.format(Locale.US, "S/ %,.2f", monto)

fun cantidad(valor: Int): String = valor.toString()

fun cantidad(valor: Double): String =
    if (valor % 1.0 == 0.0) valor.toLong().toString() else String.format(Locale.US, "%.3f", valor).trimEnd('0')
