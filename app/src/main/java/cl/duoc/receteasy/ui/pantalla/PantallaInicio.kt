package cl.duoc.receteasy.ui.pantalla

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import cl.duoc.receteasy.R
import cl.duoc.receteasy.data.model.Receta
import cl.duoc.receteasy.ui.navegacion.Rutas
import cl.duoc.receteasy.viewmodel.RecetarioViewModel
import cl.duoc.receteasy.viewmodel.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaInicio(
    navController: NavController,
    usuarioViewModel: UsuarioViewModel,
    recetarioViewModel: RecetarioViewModel
) {
    val estado by recetarioViewModel.estadoUI.collectAsState()
    val usuario = usuarioViewModel.obtenerUsuarioLogueado()
    var consulta by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "RecetEasy") },
                actions = {
                    IconButton(onClick = { navController.navigate(Rutas.CREAR) }) {
                        Icon(Icons.Default.Add, contentDescription = "Crear")
                    }

                    val avatarPainter =
                        if (usuario?.fotoUri.isNullOrBlank()) {
                            painterResource(R.drawable.default_user)
                        } else {
                            rememberAsyncImagePainter(Uri.parse(usuario!!.fotoUri!!))
                        }

                    IconButton(onClick = { /* futuro perfil */ }) {
                        Image(
                            painter = avatarPainter,
                            contentDescription = "Foto usuario",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(8.dp)
        ) {
            TextButton(
                onClick = {
                    usuarioViewModel.cerrarSesion()
                    recetarioViewModel.cerrarSesion()
                    navController.navigate(Rutas.BIENVENIDA) {
                        popUpTo(Rutas.INICIO) { inclusive = true }
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Cerrar sesión")
            }

            OutlinedTextField(
                value = consulta,
                onValueChange = {
                    consulta = it
                    recetarioViewModel.setConsulta(it)
                },
                label = { Text("Buscar por título o ingrediente (ej: ajo, sal)") },
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

            val painter =
                if (receta.imagenUri.isNullOrBlank()) {
                    painterResource(R.drawable.default_receta)
                } else {
                    rememberAsyncImagePainter(model = receta.imagenUri)
                }

            Image(
                painter = painter,
                contentDescription = "Imagen receta",
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = receta.titulo, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                val ingredientesText = receta.ingredientes
                    .joinToString(separator = ", ") { ing ->
                        "${ing.nombre} ${ing.cantidad}${if (ing.unidad.isNotBlank()) " ${ing.unidad}" else ""}"
                    }
                Text(
                    text = ingredientesText.ifBlank { "Sin ingredientes" },
                    maxLines = 2,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
