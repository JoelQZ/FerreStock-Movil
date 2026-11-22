package pe.ferrestock.app.data.remote

import kotlinx.serialization.json.JsonElement
import pe.ferrestock.app.data.remote.dto.IngresoRequestDto
import pe.ferrestock.app.data.remote.dto.IngresoResponseDto
import pe.ferrestock.app.data.remote.dto.LoginRequest
import pe.ferrestock.app.data.remote.dto.LoginResponse
import pe.ferrestock.app.data.remote.dto.ProductoDto
import pe.ferrestock.app.data.remote.dto.ReporteVentasDto
import pe.ferrestock.app.data.remote.dto.ResumenTurnoDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("api/auth/login/")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("api/catalogo/productos/")
    suspend fun getProductos(): List<ProductoDto>

    @GET("api/reportes/ventas/")
    suspend fun getReporteVentas(
        @Query("desde") desde: String,
        @Query("hasta") hasta: String
    ): ReporteVentasDto

    @GET("api/reportes/productos-mas-vendidos/")
    suspend fun getProductosMasVendidos(
        @Query("desde") desde: String,
        @Query("hasta") hasta: String,
        @Query("limite") limite: Int = 5
    ): JsonElement

    @GET("api/caja/turnos/")
    suspend fun getTurnos(): JsonElement

    @GET("api/caja/turnos/{id}/resumen/")
    suspend fun getResumenTurno(
        @Path("id") id: Int
    ): ResumenTurnoDto

    @GET("api/ventas/ventas/")
    suspend fun getVentas(): JsonElement

    @GET("api/ia/predicciones/")
    suspend fun getPredicciones(): JsonElement

    @GET("api/inventario/kardex/")
    suspend fun getKardex(): JsonElement

    @GET("api/inventario/proveedores/")
    suspend fun getProveedores(): JsonElement

    @POST("api/inventario/ingresos/")
    suspend fun registrarIngreso(
        @Body request: IngresoRequestDto
    ): IngresoResponseDto

    @GET("api/usuarios/")
    suspend fun getUsuarios(): JsonElement
}
