package cl.duoc.receteasy.ui.pantalla

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import cl.duoc.receteasy.ui.navegacion.Rutas
import cl.duoc.receteasy.viewmodel.RecetarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCrearReceta(navController: NavController, viewModel: RecetarioViewModel) {
    val estado by viewModel.estadoUI.collectAsState()

    // Campos de la receta con persistencia de estado
    var titulo by rememberSaveable() { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var ingredientes by rememberSaveable { mutableStateOf("") }
    var pasos by rememberSaveable { mutableStateOf("") }
    var imagenUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    // Recuperar foto tomada desde la pantalla de cámara usando savedStateHandle
    val fotoDesdeCamara = navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Uri>("fotoUri")
    fotoDesdeCamara?.observeForever { uri ->
        imagenUri = uri
    }

    // Selector de imagen desde galería
    val galeriaLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imagenUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Crear receta", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título*") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = ingredientes,
            onValueChange = { ingredientes = it },
            label = { Text("Ingredientes*") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = pasos,
            onValueChange = { pasos = it },
            label = { Text("Pasos") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Mostrar imagen seleccionada o tomada
        imagenUri?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = "Foto receta",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Row {
            Button(onClick = { galeriaLauncher.launch("image/*") }) {
                Text("Seleccionar imagen (Galería)")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { navController.navigate(Rutas.CAMARA) }) {
                Text("Abrir cámara")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.crearReceta(
                    titulo = titulo,
                    descripcion = descripcion,
                    ingredientesCsv = ingredientes,
                    pasos = pasos,
                    imagenUri = imagenUri,
                    creador = estado.nombreUsuario
                )
                navController.popBackStack(Rutas.INICIO, false)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar receta")
        }

        estado.error?.let { err ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = err, color = MaterialTheme.colorScheme.error)
        }
    }
}

