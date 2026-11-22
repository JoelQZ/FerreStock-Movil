package pe.ferrestock.app.ui.caja

import pe.ferrestock.app.domain.model.TurnoVista
import pe.ferrestock.app.domain.model.VentaVista

data class CajaEstado(
    val turnosAbiertos: List<TurnoVista>,
    val turnosCerrados: List<TurnoVista>,
    val ventasAnuladas: List<VentaVista>
)
