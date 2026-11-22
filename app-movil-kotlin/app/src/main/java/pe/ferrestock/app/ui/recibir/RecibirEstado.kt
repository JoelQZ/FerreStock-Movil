package pe.ferrestock.app.ui.recibir

import pe.ferrestock.app.domain.model.Producto
import pe.ferrestock.app.domain.model.ProveedorVista

data class RecibirEstado(
    val proveedores: List<ProveedorVista>,
    val productos: List<Producto>
)
