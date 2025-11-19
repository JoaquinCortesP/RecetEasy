// ✅ Navbar.kt (nuevo archivo)
package cl.duoc.receteasy.ui.componentes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import cl.duoc.receteasy.ui.navegacion.Rutas

// 🔹 Barra de navegación inferior simple
@Composable
fun Navbar(navController: NavController) {
    NavigationBar {
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(Rutas.INICIO) },
            label = { Text("Inicio") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(Rutas.CREAR) },
            label = { Text("Crear") },
            icon = { Icon(Icons.Default.Add, contentDescription = "Crear") }
        )

    }
}
