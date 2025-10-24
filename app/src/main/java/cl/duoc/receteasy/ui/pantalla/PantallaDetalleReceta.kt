package cl.duoc.receteasy.ui.pantalla

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import cl.duoc.receteasy.model.Receta
import cl.duoc.receteasy.viewmodel.RecetarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleReceta(
    navController: NavController,
    viewModel: RecetarioViewModel,
    recetaId: Long
) {
    var receta by remember { mutableStateOf<Receta?>(null) }

    LaunchedEffect(recetaId) {
        receta = viewModel.obtenerRecetaPorId(recetaId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = receta?.titulo ?: "Detalle de Receta") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás"
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (receta == null) {
            // Mientras se carga la receta
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Mostrar el contenido de la receta
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                receta?.imagenUri?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = "Imagen receta",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Text(
                    text = receta?.titulo ?: "",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text("Ingredientes", style = MaterialTheme.typography.titleMedium)
                Text(text = receta?.ingredientes ?: "Sin ingredientes")
                Spacer(modifier = Modifier.height(12.dp))

                Text("Preparación", style = MaterialTheme.typography.titleMedium)
                Text(text = receta?.pasos ?: "Sin pasos")
            }
        }
    }
}

