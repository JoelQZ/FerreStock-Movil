package pe.ferrestock.app.ui.mas

import pe.ferrestock.app.domain.model.UsuarioVista

data class PersonalEstado(
    val usuarios: List<UsuarioVista>,
    val ultimaSincronizacion: String?
)
