package pe.ferrestock.app.data.remote

import pe.ferrestock.app.data.remote.dto.LoginRequest
import pe.ferrestock.app.data.remote.dto.LoginResponse
import pe.ferrestock.app.data.remote.dto.ProductoDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("api/auth/login/")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("api/catalogo/productos/")
    suspend fun getProductos(): List<ProductoDto>
}
