package pe.ferrestock.app.data.repository

import pe.ferrestock.app.domain.model.UsuarioVista

interface UsuarioRepository {
    suspend fun getUsuarios(): Result<List<UsuarioVista>>
}
