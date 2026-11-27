package pe.ferrestock.app.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.PointOfSale
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import pe.ferrestock.app.AppContainer
import pe.ferrestock.app.R
import pe.ferrestock.app.data.remote.dto.DetalleIngresoDto
import pe.ferrestock.app.domain.model.Sesion
import pe.ferrestock.app.ui.UiState
import pe.ferrestock.app.ui.caja.CajaScreen
import pe.ferrestock.app.ui.caja.CajaViewModel
import pe.ferrestock.app.ui.inicio.InicioScreen
import pe.ferrestock.app.ui.inicio.InicioViewModel
import pe.ferrestock.app.ui.login.LoginScreen
import pe.ferrestock.app.ui.login.LoginViewModel
import pe.ferrestock.app.ui.mas.PersonalScreen
import pe.ferrestock.app.ui.mas.PersonalViewModel
import pe.ferrestock.app.ui.recibir.RecibirScreen
import pe.ferrestock.app.ui.recibir.RecibirViewModel
import pe.ferrestock.app.ui.stock.DetalleProductoScreen
import pe.ferrestock.app.ui.stock.DetalleViewModel
import pe.ferrestock.app.ui.stock.StockScreen
import pe.ferrestock.app.ui.stock.StockViewModel
import pe.ferrestock.app.ui.theme.Madera
import pe.ferrestock.app.ui.theme.MaderaOscura
import pe.ferrestock.app.ui.theme.Tarjeta

object Rutas {
    const val LOGIN = "login"
    const val INICIO = "inicio"
    const val CAJA = "caja"
    const val STOCK = "stock"
    const val RECIBIR = "recibir"
    const val DETALLE = "detalle/{id}"
    const val PERSONAL = "personal"

    fun detalle(id: Int) = "detalle/$id"
}

data class Pestana(val ruta: String, @StringRes val titulo: Int, val icono: ImageVector)

private val pestanas = listOf(
    Pestana(Rutas.INICIO, R.string.nav_inicio, Icons.Outlined.Home),
    Pestana(Rutas.CAJA, R.string.nav_caja, Icons.Outlined.PointOfSale),
    Pestana(Rutas.STOCK, R.string.nav_stock, Icons.Outlined.Inventory2),
    Pestana(Rutas.RECIBIR, R.string.nav_recibir, Icons.Outlined.AddBox)
)

@Composable
fun AppNav(container: AppContainer) {
    val navController = rememberNavController()
    val contexto = LocalContext.current
    val loginViewModel: LoginViewModel = viewModel(factory = LoginViewModel.Factory(container))
    val estadoLogin by loginViewModel.estado.collectAsStateWithLifecycle()
    var sesionCerrada by rememberSaveable { mutableStateOf(false) }
    val sesion = if (sesionCerrada) null else (estadoLogin as? UiState.Exito<Sesion>)?.datos
    val entrada by navController.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route
    val avisos = remember { SnackbarHostState() }
    val cajon = rememberDrawerState(DrawerValue.Closed)
    val alcance = rememberCoroutineScope()
    val abrirMenu: () -> Unit = { alcance.launch { cajon.open() } }
    val enPestana = pestanas.any { it.ruta == rutaActual }
    val stockViewModel: StockViewModel = viewModel(factory = StockViewModel.Factory(container))
    val estadoStock by stockViewModel.estado.collectAsStateWithLifecycle()
    val avisoStock by stockViewModel.aviso.collectAsStateWithLifecycle()
    LaunchedEffect(avisoStock) { avisoStock?.let { avisos.showSnackbar(it) } }

    ModalNavigationDrawer(
        drawerState = cajon,
        gesturesEnabled = enPestana || cajon.isOpen,
        drawerContent = {
            MenuLateral(
                sesion = sesion,
                ultimaSincronizacion = (estadoStock as? UiState.Exito)?.datos?.ultimaSincronizacion,
                onPersonal = {
                    alcance.launch { cajon.close() }
                    navController.navigate(Rutas.PERSONAL)
                },
                onSincronizar = {
                    alcance.launch { cajon.close() }
                    stockViewModel.sincronizar()
                },
                onCerrarSesion = {
                    alcance.launch { cajon.close() }
                    sesionCerrada = true
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }
    ) {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = { SnackbarHost(avisos) },
            bottomBar = {
                if (enPestana) {
                    BarraInferior(rutaActual) { ruta -> irAPestana(navController, ruta) }
                }
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = Rutas.LOGIN,
                modifier = Modifier.padding(padding)
            ) {
                composable(Rutas.LOGIN) {
                    LoginScreen(
                        viewModel = loginViewModel,
                        onLoginExitoso = {
                            sesionCerrada = false
                            navController.navigate(Rutas.INICIO) {
                                popUpTo(Rutas.LOGIN) { inclusive = true }
                            }
                        }
                    )
                }
                composable(Rutas.INICIO) {
                    val inicioViewModel: InicioViewModel = viewModel(factory = InicioViewModel.Factory(contexto, container))
                    val estado by inicioViewModel.estado.collectAsStateWithLifecycle()
                    val periodo by inicioViewModel.periodo.collectAsStateWithLifecycle()
                    InicioScreen(
                        nombre = sesion?.usuario.orEmpty(),
                        estado = estado,
                        periodo = periodo,
                        onPeriodo = inicioViewModel::cambiarPeriodo,
                        onReintentar = inicioViewModel::cargarDatos,
                        onMenu = abrirMenu
                    )
                    RecargarAlVolver(inicioViewModel::cargarDatos)
                }
                composable(Rutas.CAJA) {
                    val cajaViewModel: CajaViewModel = viewModel(factory = CajaViewModel.Factory(container))
                    val estado by cajaViewModel.estado.collectAsStateWithLifecycle()
                    CajaScreen(estado = estado, onReintentar = cajaViewModel::cargar, onMenu = abrirMenu)
                    RecargarAlVolver(cajaViewModel::cargar)
                }
                composable(Rutas.STOCK) {
                    LaunchedEffect(Unit) { stockViewModel.sincronizar() }
                    StockScreen(
                        estado = estadoStock,
                        onProducto = { id -> navController.navigate(Rutas.detalle(id)) },
                        onReintentar = stockViewModel::sincronizar,
                        onMenu = abrirMenu
                    )
                }
                composable(
                    route = Rutas.DETALLE,
                    arguments = listOf(navArgument("id") { type = NavType.IntType })
                ) { entradaDetalle ->
                    val id = entradaDetalle.arguments?.getInt("id") ?: 0
                    val detalleViewModel: DetalleViewModel = viewModel(factory = DetalleViewModel.Factory(id, container))
                    val estado by detalleViewModel.estado.collectAsStateWithLifecycle()
                    DetalleProductoScreen(
                        estado = estado,
                        onVolver = { navController.popBackStack() },
                        onReintentar = detalleViewModel::cargar
                    )
                }
                composable(Rutas.RECIBIR) {
                    val recibirViewModel: RecibirViewModel = viewModel(factory = RecibirViewModel.Factory(container))
                    val estado by recibirViewModel.estado.collectAsStateWithLifecycle()
                    val mensaje by recibirViewModel.mensaje.collectAsStateWithLifecycle()
                    LaunchedEffect(mensaje) { mensaje?.let { avisos.showSnackbar(it) } }
                    RecibirScreen(
                        estado = estado,
                        onRegistrar = { ingreso ->
                            recibirViewModel.registrar(
                                proveedorId = ingreso.proveedorId,
                                nroDocumento = ingreso.nroDocumento,
                                detalles = ingreso.lineas.map { linea ->
                                    DetalleIngresoDto(
                                        producto = linea.producto.id,
                                        cantidad = linea.cantidad.toString(),
                                        costoUnitario = linea.costoUnitario.toString()
                                    )
                                }
                            )
                        },
                        onReintentar = recibirViewModel::cargar,
                        onMenu = abrirMenu
                    )
                    RecargarAlVolver(recibirViewModel::cargar)
                }
                composable(Rutas.PERSONAL) {
                    val personalViewModel: PersonalViewModel = viewModel(factory = PersonalViewModel.Factory(container))
                    val estado by personalViewModel.estado.collectAsStateWithLifecycle()
                    PersonalScreen(
                        estado = estado,
                        onVolver = { navController.popBackStack() },
                        onReintentar = personalViewModel::cargar
                    )
                }
            }
        }
    }
}

@Composable
private fun RecargarAlVolver(onRecargar: () -> Unit) {
    var visitada by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (visitada) onRecargar() else visitada = true
    }
}

private fun irAPestana(navController: NavHostController, ruta: String) {
    navController.navigate(ruta) {
        popUpTo(Rutas.INICIO) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun BarraInferior(rutaActual: String?, onSeleccionar: (String) -> Unit) {
    NavigationBar(containerColor = Tarjeta) {
        pestanas.forEach { pestana ->
            NavigationBarItem(
                selected = rutaActual == pestana.ruta,
                onClick = { onSeleccionar(pestana.ruta) },
                icon = { Icon(pestana.icono, contentDescription = null) },
                label = { Text(stringResource(pestana.titulo)) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaderaOscura,
                    selectedTextColor = Madera,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
