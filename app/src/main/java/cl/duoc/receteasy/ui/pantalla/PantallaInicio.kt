// ✅ PantallaInicio.kt
package cl.duoc.receteasy.ui.pantalla

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import cl.duoc.receteasy.model.Receta
import cl.duoc.receteasy.ui.navegacion.Rutas
import cl.duoc.receteasy.viewmodel.RecetarioViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaInicio(navController: NavController, viewModel: RecetarioViewModel) {
    val estado by viewModel.estadoUI.collectAsState()
    var consulta by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // 🔹 Estructura visual principal
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "RecetEasy") },
                actions = {
                    IconButton(onClick = { navController.navigate(Rutas.CREAR) }) {
                        Icon(Icons.Default.Add, contentDescription = "Crear")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(8.dp)
        ) {
            OutlinedTextField(
                value = consulta,
                onValueChange = {
                    consulta = it
                    viewModel.setConsulta(it)
                },
                label = { Text("Buscar por título o ingrediente") },
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedVisibility(
                visible = estado.cargando,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 🔹 Listado de recetas
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(estado.recetas) { receta ->
                    FilaReceta(receta = receta, onClick = {
                        navController.navigate("${Rutas.DETALLE_BASE}/${receta.id}")
                    })
                }
            }
        }
    }
}

@Composable
fun FilaReceta(receta: Receta, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            val painter = rememberAsyncImagePainter(model = receta.imagenUri)
            Image(
                painter = painter,
                contentDescription = "Imagen receta",
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = receta.titulo, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Ingredientes: ${receta.ingredientes}",
                    maxLines = 2,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
