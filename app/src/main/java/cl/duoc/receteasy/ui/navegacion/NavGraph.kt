package cl.duoc.receteasy.ui.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duoc.receteasy.ui.pantalla.*
import cl.duoc.receteasy.viewmodel.RecetarioViewModel
import cl.duoc.receteasy.viewmodel.UsuarioViewModel

@Composable
fun NavGraph(
    recetarioViewModel: RecetarioViewModel,
    usuarioViewModel: UsuarioViewModel,
    startDestination: String = Rutas.BIENVENIDA
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Rutas.BIENVENIDA) {
            PantallaBienvenida(navController)
        }
        composable(Rutas.REGISTRO) {
            PantallaRegistro(navController, usuarioViewModel)
        }
        composable(Rutas.LOGIN) {
            PantallaLogin(navController, usuarioViewModel)
        }
        composable(Rutas.INICIO) {
            PantallaInicio(
                navController = navController,
                usuarioViewModel = usuarioViewModel,
                recetarioViewModel = recetarioViewModel

            )
        }
        composable(Rutas.CREAR) {
            PantallaCrearReceta(navController, recetarioViewModel)
        }
        composable(
            route = "${Rutas.DETALLE_BASE}/{recetaId}",
            arguments = listOf(navArgument("recetaId") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("recetaId") ?: 0L
            PantallaDetalleReceta(navController, recetarioViewModel, id)
        }
        composable(Rutas.CAMARA) {
            PantallaCamara(navController = navController) { uri ->
                navController.previousBackStackEntry?.savedStateHandle?.set("fotoUri", uri)
                navController.popBackStack()
            }
        }
        composable(Rutas.INGREDIENTES_XANO) {
            PantallaListaIngredientesRemotos(navController = navController)
        }
    }
}
