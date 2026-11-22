package pe.ferrestock.app.data.repository

import pe.ferrestock.app.domain.model.Sesion

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<Sesion>
}
