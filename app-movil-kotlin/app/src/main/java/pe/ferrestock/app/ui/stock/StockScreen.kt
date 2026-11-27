package pe.ferrestock.app.ui.stock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import pe.ferrestock.app.domain.model.PrediccionVista
import pe.ferrestock.app.domain.model.Producto
import pe.ferrestock.app.ui.UiState
import pe.ferrestock.app.ui.components.Buscador
import pe.ferrestock.app.ui.components.ChipsFiltro
import pe.ferrestock.app.ui.components.ContenidoSegunEstado
import pe.ferrestock.app.ui.components.Encabezado
import pe.ferrestock.app.ui.components.EtiquetaStock
import pe.ferrestock.app.ui.components.Tarjeta
import pe.ferrestock.app.ui.components.Vacio
import pe.ferrestock.app.ui.components.cantidad
import pe.ferrestock.app.ui.components.soles
import pe.ferrestock.app.ui.theme.MaderaOscura

private enum class FiltroStock(val texto: String) {
    TODOS("Todos"),
    REPONER("Reponer"),
    IA("Sugerencia IA")
}

@Composable
fun StockScreen(
    estado: UiState<StockEstado>,
    onProducto: (Int) -> Unit,
    onReintentar: () -> Unit,
    onMenu: () -> Unit
) {
    var filtro by rememberSaveable { mutableStateOf(FiltroStock.TODOS) }
    var busqueda by rememberSaveable { mutableStateOf("") }
    val datosActuales = (estado as? UiState.Exito)?.datos
    val subtitulo = datosActuales?.let { datos ->
        listOfNotNull("${datos.productos.size} productos", datos.ultimaSincronizacion?.let { "sincronizado $it" })
            .joinToString(" · ")
    }

    Column(Modifier.fillMaxSize()) {
        Encabezado("Stock", subtitulo, onMenu = onMenu)
        Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Buscador(busqueda, { busqueda = it })
            ChipsFiltro(FiltroStock.entries, filtro, { it.texto }, { filtro = it })
        }
        ContenidoSegunEstado(estado, onReintentar) { datos ->
            if (filtro == FiltroStock.IA) {
                val lista = datos.predicciones.filter { coincide(it.productoNombre, it.productoCodigo, busqueda) }
                if (lista.isEmpty()) Vacio("Sin sugerencias", "La IA no encontró productos por reponer")
                else ListaPredicciones(lista, onProducto)
            } else {
                val lista = datos.productos
                    .filter { filtro == FiltroStock.TODOS || it.stock <= it.stockMinimo }
                    .filter { coincide(it.nombre, it.codigo, busqueda) }
                if (lista.isEmpty()) Vacio("No hay productos", "Prueba con otra búsqueda o sincroniza")
                else ListaProductos(lista, onProducto)
            }
        }
    }
}

private fun coincide(nombre: String, codigo: String, busqueda: String) =
    busqueda.isBlank() || nombre.contains(busqueda, ignoreCase = true) || codigo.contains(busqueda, ignoreCase = true)

@Composable
private fun ListaProductos(productos: List<Producto>, onProducto: (Int) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(productos, key = { it.id }) { producto ->
            Tarjeta(onClick = { onProducto(producto.id) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(producto.nombre, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(
                            "${producto.codigo} · ${cantidad(producto.stock)} de mín. ${cantidad(producto.stockMinimo)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(soles(producto.precio), style = MaterialTheme.typography.labelLarge)
                        EtiquetaStock(producto)
                    }
                }
            }
        }
    }
}

@Composable
private fun ListaPredicciones(predicciones: List<PrediccionVista>, onProducto: (Int) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(predicciones, key = { it.productoId }) { prediccion ->
            Tarjeta(onClick = { onProducto(prediccion.productoId) }) {
                Text(prediccion.productoNombre, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    "Stock ${cantidad(prediccion.stockActual)} · demanda estimada ${cantidad(prediccion.demandaEstimada)} ·se agota en ${prediccion.diasParaAgotarse} días",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Pedir ${cantidad(prediccion.cantidadSugerida)} unidades",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaderaOscura,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}
