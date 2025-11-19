package cl.duoc.receteasy.ui.pantalla

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import cl.duoc.receteasy.R
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .systemBarsPadding()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            val painter =
                if (receta!!.imagenUri.isNullOrBlank() || receta!!.imagenUri == "default_receta.png") {
                    painterResource(R.drawable.default_receta)
                } else {
                    rememberAsyncImagePainter(receta!!.imagenUri)
                }

            Image(
                painter = painter,
                contentDescription = "Imagen receta",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = receta!!.titulo,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Ingredientes", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = receta!!.ingredientes.joinToString("\n") { ing ->
                    "- ${ing.nombre} ${ing.cantidad} ${ing.unidad}"
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Preparación", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(6.dp))

            Text(text = receta!!.pasos)
        }
    }
}
