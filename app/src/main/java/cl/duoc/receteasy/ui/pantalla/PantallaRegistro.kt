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
fun PantallaRegistro(navController: NavController, usuarioViewModel: UsuarioViewModel) {
    val context = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var fotoUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var acceptTerms by remember { mutableStateOf(false) }

    fun crearArchivoTemporal(): File {
        val file = File.createTempFile("usuario_foto_", ".jpg", context.cacheDir)
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
        Text("Registro de Usuario", style = MaterialTheme.typography.headlineSmall)
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

        Spacer(modifier = Modifier.height(20.dp))

        if (bitmap != null) {
            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = "Foto usuario",
                modifier = Modifier.size(150.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(onClick = { permisoCamaraLauncher.launch(Manifest.permission.CAMERA) }) {
            Text("Tomar foto de usuario")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = acceptTerms,
                onCheckedChange = { acceptTerms = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Acepto términos y condiciones")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (!acceptTerms) {
                    mensaje = "Debes aceptar los términos y condiciones"
                    return@Button
                }
                if (fotoUri == null) {
                    mensaje = "Debes tomar una foto antes de registrarte"
                } else {
                    usuarioViewModel.registrarUsuario(
                        nombre,
                        contrasena,
                        fotoUri
                    ) { exito, msg ->
                        mensaje = msg
                        if (exito) navController.navigate("login") {
                            popUpTo("registro") { inclusive = true }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar")
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(text = mensaje, color = MaterialTheme.colorScheme.primary)
    }
}

