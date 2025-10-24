package cl.duoc.receteasy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import cl.duoc.receteasy.repository.BaseDeDatos
import cl.duoc.receteasy.repository.RecetaRepository
import cl.duoc.receteasy.ui.navegacion.NavGraph
import cl.duoc.receteasy.ui.theme.RecetEasyTheme
import cl.duoc.receteasy.viewmodel.RecetarioViewModel
import cl.duoc.receteasy.viewmodel.UsuarioViewModel

class MainActivity : ComponentActivity() {

    private lateinit var recetarioViewModel: RecetarioViewModel
    private lateinit var usuarioViewModel: UsuarioViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa base de datos y repositorio
        val bd = BaseDeDatos.obtenerInstancia(applicationContext)
        val repo = RecetaRepository(bd.recetaDao())

        // Crea RecetarioViewModel con fábrica
        val fabrica = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return RecetarioViewModel(repo) as T
            }
        }

        recetarioViewModel = ViewModelProvider(this, fabrica)[RecetarioViewModel::class.java]

        // Crea UsuarioViewModel usando AndroidViewModelFactory
        usuarioViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[UsuarioViewModel::class.java]

        // 🔹 UI principal
        setContent {
            RecetEasyTheme {
                NavGraph(
                    recetarioViewModel = recetarioViewModel,
                    usuarioViewModel = usuarioViewModel
                )
            }
        }
    }
}

