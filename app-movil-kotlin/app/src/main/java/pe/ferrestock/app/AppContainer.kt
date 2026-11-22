package pe.ferrestock.app

import android.content.Context
import androidx.room.Room
import pe.ferrestock.app.data.local.AppDatabase
import pe.ferrestock.app.data.remote.RetrofitClient
import pe.ferrestock.app.data.repository.AuthRepository
import pe.ferrestock.app.data.repository.AuthRepositoryImpl
import pe.ferrestock.app.data.repository.ProductoRepository
import pe.ferrestock.app.data.repository.ProductoRepositoryImpl

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
}
