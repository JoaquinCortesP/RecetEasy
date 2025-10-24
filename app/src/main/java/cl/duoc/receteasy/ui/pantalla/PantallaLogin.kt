package cl.duoc.receteasy.ui.pantalla

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import cl.duoc.receteasy.viewmodel.UsuarioViewModel
import java.io.File

@Composable
fun PantallaLogin(navController: NavController, usuarioViewModel: UsuarioViewModel) {
    val context = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var fotoUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    fun crearArchivoTemporal(): File {
        val file = File.createTempFile("login_foto_", ".jpg", context.cacheDir)
        file.deleteOnExit()
        return file
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && fotoUri != null) {
            val bmp = MediaStore.Images.Media.getBitmap(context.contentResolver, fotoUri)
            bitmap = bmp
        }
    }

    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val archivo = crearArchivoTemporal()
            val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", archivo)
            fotoUri = uri
            takePictureLauncher.launch(uri)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Inicio de Sesión", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre de usuario") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (bitmap != null) {
            Image(bitmap = bitmap!!.asImageBitmap(), contentDescription = "Foto biométrica", modifier = Modifier.size(150.dp))
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(onClick = { permisoCamaraLauncher.launch(Manifest.permission.CAMERA) }) {
            Text("Tomar foto (verificación)")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (bitmap == null) {
                    mensaje = "Debes tomarte una foto para verificarte"
                } else {
                    usuarioViewModel.iniciarSesion(nombre, contrasena) { exito, msg ->
                        mensaje = msg
                        if (exito) {
                            navController.navigate("inicio") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Entrar")
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(text = mensaje, color = MaterialTheme.colorScheme.primary)
    }
}


