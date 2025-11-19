// app/src/main/java/cl/duoc/receteasy/MainActivity.kt
package cl.duoc.receteasy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.addCallback
import androidx.lifecycle.ViewModelProvider
import cl.duoc.receteasy.repository.BaseDeDatos
import cl.duoc.receteasy.repository.RecetaRepository
import cl.duoc.receteasy.ui.navegacion.NavGraph
import cl.duoc.receteasy.ui.navegacion.Rutas
import cl.duoc.receteasy.ui.theme.RecetEasyTheme
import cl.duoc.receteasy.viewmodel.RecetarioViewModel
import cl.duoc.receteasy.viewmodel.UsuarioViewModel

class MainActivity : ComponentActivity() {

    private lateinit var recetarioViewModel: RecetarioViewModel
    private lateinit var usuarioViewModel: UsuarioViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val bd = BaseDeDatos.obtenerInstancia(applicationContext)
        val repo = RecetaRepository(bd.recetaDao())

        val fabrica = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return RecetarioViewModel(repo) as T
            }
        }

        recetarioViewModel = ViewModelProvider(this, fabrica)[RecetarioViewModel::class.java]

        usuarioViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[UsuarioViewModel::class.java]

        val rutaInicial = if (usuarioViewModel.obtenerUsuarioLogueado() != null) {
            Rutas.INICIO
        } else {
            Rutas.BIENVENIDA
        }

        // Consumir el botón físico atrás (evitar volver al login)
        onBackPressedDispatcher.addCallback(this) {
            // No-op: consumimos el evento para que la app no navegue atrás automáticamente.
            // Si quieres permitir salir de la app con el botón atrás, reemplaza el cuerpo por: isEnabled = false; activity?.finish()
        }

        setContent {
            RecetEasyTheme {
                NavGraph(
                    recetarioViewModel = recetarioViewModel,
                    usuarioViewModel = usuarioViewModel,
                    startDestination = rutaInicial
                )
            }
        }
    }
}
