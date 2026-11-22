package pe.ferrestock.app

import android.content.Context
import androidx.room.Room
import pe.ferrestock.app.data.local.AppDatabase
import pe.ferrestock.app.data.remote.RetrofitClient
import pe.ferrestock.app.data.repository.AuthRepository
import pe.ferrestock.app.data.repository.AuthRepositoryImpl
import pe.ferrestock.app.data.repository.CajaRepository
import pe.ferrestock.app.data.repository.CajaRepositoryImpl
import pe.ferrestock.app.data.repository.InventarioRepository
import pe.ferrestock.app.data.repository.InventarioRepositoryImpl
import pe.ferrestock.app.data.repository.ProductoRepository
import pe.ferrestock.app.data.repository.ProductoRepositoryImpl
import pe.ferrestock.app.data.repository.ReporteRepository
import pe.ferrestock.app.data.repository.ReporteRepositoryImpl
import pe.ferrestock.app.data.repository.UsuarioRepository
import pe.ferrestock.app.data.repository.UsuarioRepositoryImpl

class AppContainer(context: Context) {
    private val database by lazy {
        Room.databaseBuilder(context, AppDatabase::class.java, "ferrestock.db")
            .fallbackToDestructiveMigration(true)
            .build()
    }

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(RetrofitClient.apiService)
    }

    val productoRepository: ProductoRepository by lazy {
        ProductoRepositoryImpl(
            apiService = RetrofitClient.apiService,
            productoDao = database.productoDao()
        )
    }

    val reporteRepository: ReporteRepository by lazy {
        ReporteRepositoryImpl(RetrofitClient.apiService)
    }

    val cajaRepository: CajaRepository by lazy {
        CajaRepositoryImpl(RetrofitClient.apiService)
    }

    val inventarioRepository: InventarioRepository by lazy {
        InventarioRepositoryImpl(RetrofitClient.apiService)
    }

    val usuarioRepository: UsuarioRepository by lazy {
        UsuarioRepositoryImpl(RetrofitClient.apiService)
    }
}
