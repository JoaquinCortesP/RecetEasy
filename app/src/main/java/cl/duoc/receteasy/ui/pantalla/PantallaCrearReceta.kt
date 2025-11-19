package cl.duoc.receteasy.ui.pantalla

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import cl.duoc.receteasy.ui.navegacion.Rutas
import cl.duoc.receteasy.viewmodel.RecetarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCrearReceta(navController: NavController, viewModel: RecetarioViewModel) {
    val estado by viewModel.estadoUI.collectAsState()

    var titulo by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var pasos by rememberSaveable { mutableStateOf("") }
    var imagenUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    // NUEVO: estado de error para pasos
    var pasosError by rememberSaveable { mutableStateOf(false) }

    var nombreIng by remember { mutableStateOf("") }
    var cantidadIng by remember { mutableStateOf("") }
    var unidadIng by remember { mutableStateOf("gr") }
    val unidades = listOf("gr", "kg", "ml", "cc", "unidad")
    var mostrarMenuUnidades by remember { mutableStateOf(false) }

    val savedHandle = navController.currentBackStackEntry?.savedStateHandle
    val fotoFromSaved by savedHandle
        ?.getStateFlow<Uri?>("fotoUri", null)
        ?.collectAsState(initial = null)
        ?: remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(fotoFromSaved) {
        if (fotoFromSaved != null) imagenUri = fotoFromSaved
    }

    val galeriaLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imagenUri = uri }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
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
        Spacer(modifier = Modifier.height(12.dp))

        Text("Agregar ingrediente", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = nombreIng,
            onValueChange = { nombreIng = it },
            label = { Text("Nombre ingrediente*") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = cantidadIng,
                onValueChange = { cantidadIng = it },
                label = { Text("Cantidad*") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            ExposedDropdownMenuBox(
                expanded = mostrarMenuUnidades,
                onExpandedChange = { mostrarMenuUnidades = !mostrarMenuUnidades },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = unidadIng,
                    onValueChange = {},
                    label = { Text("Unidad") },
                    readOnly = true,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                DropdownMenu(
                    expanded = mostrarMenuUnidades,
                    onDismissRequest = { mostrarMenuUnidades = false }
                ) {
                    unidades.forEach { u ->
                        DropdownMenuItem(
                            text = { Text(u) },
                            onClick = {
                                unidadIng = u
                                mostrarMenuUnidades = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (nombreIng.isBlank()) {
                    viewModel.setError("Nombre de ingrediente vacío")
                    return@Button
                }
                if (cantidadIng.isBlank()) {
                    viewModel.setError("Cantidad de ingrediente vacía")
                    return@Button
                }
                val cantidad = cantidadIng.toDoubleOrNull()
                if (cantidad == null) {
                    viewModel.setError("Cantidad no válida")
                    return@Button
                }
                viewModel.limpiarError()
                viewModel.agregarIngrediente(nombreIng, cantidad, unidadIng)
                nombreIng = ""
                cantidadIng = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar ingrediente")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("Ingredientes agregados:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        viewModel.ingredientesTemp.forEach { ing ->
            Text("- ${ing.nombre}  ${ing.cantidad} ${ing.unidad}")
        }

        Spacer(modifier = Modifier.height(20.dp))

        //  Campo de pasos obligatorio con error visual
        OutlinedTextField(
            value = pasos,
            onValueChange = {
                pasos = it
                if (pasosError && it.isNotBlank()) {
                    pasosError = false
                }
            },
            label = { Text("Pasos*") },
            isError = pasosError,
            modifier = Modifier.fillMaxWidth()
        )

        if (pasosError) {
            Text(
                text = "Debes ingresar los pasos de la receta",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

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

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                viewModel.limpiarError()

                //Validación: pasos obligatorio
                if (pasos.isBlank()) {
                    pasosError = true
                    viewModel.setError("Debes ingresar los pasos de la receta")
                    return@Button
                }

                val ok = viewModel.crearReceta(
                    titulo = titulo,
                    descripcion = descripcion,
                    pasos = pasos,
                    imagenUri = imagenUri,
                    creador = estado.nombreUsuario
                )

                if (ok) {
                    titulo = ""
                    descripcion = ""
                    pasos = ""
                    imagenUri = null
                    pasosError = false
                    navController.popBackStack(Rutas.INICIO, false)
                }
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

