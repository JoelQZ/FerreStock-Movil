package pe.ferrestock.app.ui.recibir

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import pe.ferrestock.app.domain.model.Producto
import pe.ferrestock.app.domain.model.ProveedorVista
import pe.ferrestock.app.ui.UiState
import pe.ferrestock.app.ui.components.Buscador
import pe.ferrestock.app.ui.components.ContenidoSegunEstado
import pe.ferrestock.app.ui.components.Encabezado
import pe.ferrestock.app.ui.components.Tarjeta
import pe.ferrestock.app.ui.components.TituloSeccion
import pe.ferrestock.app.ui.components.cantidad
import pe.ferrestock.app.ui.components.coloresCampo
import pe.ferrestock.app.ui.components.soles
import pe.ferrestock.app.ui.theme.Borde
import pe.ferrestock.app.ui.theme.Madera
import pe.ferrestock.app.ui.theme.MaderaOscura
import pe.ferrestock.app.ui.theme.Tarjeta as ColorTarjeta

data class LineaIngreso(
    val producto: Producto,
    val cantidad: Double,
    val costoUnitario: Double
)

data class IngresoBorrador(
    val proveedorId: Int,
    val nroDocumento: String,
    val lineas: List<LineaIngreso>
)

private class LineaEditable(val producto: Producto, cantidad: String, costo: String) {
    var cantidad by mutableStateOf(cantidad)
    var costo by mutableStateOf(costo)
    val cantidadNumero: Double get() = cantidad.toDoubleOrNull() ?: 0.0
    val costoNumero: Double get() = costo.toDoubleOrNull() ?: 0.0
}

@Composable
fun RecibirScreen(
    estado: UiState<RecibirEstado>,
    onRegistrar: (IngresoBorrador) -> Unit,
    onReintentar: () -> Unit,
    onMenu: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        Encabezado("Recibir", "Ingreso de mercadería", onMenu = onMenu)
        ContenidoSegunEstado(estado, onReintentar) { datos -> FormularioIngreso(datos, onRegistrar) }
    }
}

@Composable
private fun FormularioIngreso(datos: RecibirEstado, onRegistrar: (IngresoBorrador) -> Unit) {
    var proveedor by remember { mutableStateOf<ProveedorVista?>(null) }
    var documento by rememberSaveable { mutableStateOf("") }
    val lineas = remember { mutableStateListOf<LineaEditable>() }
    var eligiendo by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val total = lineas.sumOf { it.cantidadNumero * it.costoNumero }

    Column(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                SelectorProveedor(datos.proveedores.filter { it.estado }, proveedor) {
                    proveedor = it
                    error = null
                }
            }
            item {
                OutlinedTextField(
                    value = documento,
                    onValueChange = { documento = it },
                    label = { Text("N.º de guía o factura") },
                    singleLine = true,
                    colors = coloresCampo(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item { TituloSeccion("Productos (${lineas.size})") }
            itemsIndexed(lineas, key = { _, linea -> linea.producto.id }) { _, linea ->
                TarjetaLinea(linea, onQuitar = { lineas.remove(linea) })
            }
            item {
                OutlinedButton(
                    onClick = { eligiendo = true },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, Madera)
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = null, tint = MaderaOscura)
                    Text("Agregar producto", color = MaderaOscura, modifier = Modifier.padding(start = 6.dp))
                }
            }
        }
        Column(
            Modifier
                .fillMaxWidth()
                .background(ColorTarjeta)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Total del ingreso", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                Text(soles(total), style = MaterialTheme.typography.titleLarge)
            }
            error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium) }
            Button(
                onClick = {
                    error = validar(proveedor, lineas)
                    val elegido = proveedor
                    if (error == null && elegido != null) {
                        onRegistrar(
                            IngresoBorrador(
                                proveedorId = elegido.idProveedor,
                                nroDocumento = documento.trim(),
                                lineas = lineas.map { LineaIngreso(it.producto, it.cantidadNumero, it.costoNumero) }
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("REGISTRAR INGRESO")
            }
        }
    }

    if (eligiendo) {
        DialogoProductos(
            productos = datos.productos.filter { p -> lineas.none { it.producto.id == p.id } },
            onElegir = { producto ->
                lineas.add(LineaEditable(producto, "1", ""))
                eligiendo = false
                error = null
            },
            onCerrar = { eligiendo = false }
        )
    }
}

private fun validar(proveedor: ProveedorVista?, lineas: List<LineaEditable>): String? = when {
    proveedor == null -> "Elige el proveedor"
    lineas.isEmpty() -> "Agrega al menos un producto"
    lineas.any { it.cantidadNumero <= 0.0 } -> "Revisa las cantidades"
    lineas.any { it.costo.toDoubleOrNull() == null || it.costoNumero < 0.0 } -> "Revisa los costos unitarios"
    else -> null
}

@Composable
private fun SelectorProveedor(
    proveedores: List<ProveedorVista>,
    seleccionado: ProveedorVista?,
    onElegir: (ProveedorVista) -> Unit
) {
    var abierto by remember { mutableStateOf(false) }
    Box {
        Tarjeta(onClick = { abierto = true }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Proveedor", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(seleccionado?.razonSocial ?: "Elegir proveedor", style = MaterialTheme.typography.bodyLarge)
                    seleccionado?.let {
                        Text("RUC ${it.ruc}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Icon(Icons.Outlined.ArrowDropDown, contentDescription = null)
            }
        }
        DropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            proveedores.forEach { proveedor ->
                DropdownMenuItem(
                    text = { Text(proveedor.razonSocial) },
                    onClick = {
                        onElegir(proveedor)
                        abierto = false
                    }
                )
            }
        }
    }
}

@Composable
private fun TarjetaLinea(linea: LineaEditable, onQuitar: () -> Unit) {
    Tarjeta {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(linea.producto.nombre, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    "${linea.producto.codigo} · stock ${cantidad(linea.producto.stock)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onQuitar) {
                Icon(Icons.Outlined.DeleteOutline, contentDescription = "Quitar", tint = MaterialTheme.colorScheme.error)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = linea.cantidad,
                onValueChange = { linea.cantidad = it },
                label = { Text("Cantidad") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = coloresCampo(),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = linea.costo,
                onValueChange = { linea.costo = it },
                label = { Text("Costo S/") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = coloresCampo(),
                modifier = Modifier.weight(1f)
            )
        }
        Text(
            "Subtotal ${soles(linea.cantidadNumero * linea.costoNumero)}",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 6.dp)
        )
    }
}

@Composable
private fun DialogoProductos(productos: List<Producto>, onElegir: (Producto) -> Unit, onCerrar: () -> Unit) {
    var busqueda by remember { mutableStateOf("") }
    val lista = productos.filter {
        busqueda.isBlank() || it.nombre.contains(busqueda, ignoreCase = true) || it.codigo.contains(busqueda, ignoreCase = true)
    }
    AlertDialog(
        onDismissRequest = onCerrar,
        confirmButton = {},
        dismissButton = { TextButton(onClick = onCerrar) { Text("Cerrar") } },
        title = { Text("Agregar producto") },
        containerColor = ColorTarjeta,
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Buscador(busqueda, { busqueda = it }, pista = "Buscar")
                LazyColumn(Modifier.heightIn(max = 320.dp)) {
                    items(lista, key = { it.id }) { producto ->
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .clickable { onElegir(producto) }
                                .padding(vertical = 10.dp)
                        ) {
                            Text(producto.nombre, style = MaterialTheme.typography.bodyLarge)
                            Text(
                                "${producto.codigo} · stock ${cantidad(producto.stock)}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        androidx.compose.material3.HorizontalDivider(color = Borde)
                    }
                }
            }
        }
    )
}
