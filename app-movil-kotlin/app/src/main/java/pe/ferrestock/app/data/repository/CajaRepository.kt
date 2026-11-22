package pe.ferrestock.app.data.repository

import pe.ferrestock.app.domain.model.TurnoVista
import pe.ferrestock.app.domain.model.VentaVista

interface CajaRepository {
    suspend fun getTurnos(): Result<List<TurnoVista>>
    suspend fun getResumenTurno(id: Int): Result<Double>
    suspend fun getVentasAnuladas(): Result<List<VentaVista>>
}
