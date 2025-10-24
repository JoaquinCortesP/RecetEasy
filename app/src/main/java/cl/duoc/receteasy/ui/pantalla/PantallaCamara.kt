package cl.duoc.receteasy.ui.pantalla

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import java.io.File

@Composable
fun PantallaCamara(
    navController: androidx.navigation.NavController,
    onFotoTomada: (Uri) -> Unit
) {
    val context = LocalContext.current
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    var permisoConcedido by remember { mutableStateOf(false) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (!success) imagenUri = null
    }

    val permisoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        permisoConcedido = granted
        if (granted) {
            val archivo = File.createTempFile("receta_foto_", ".jpg", context.cacheDir)
            archivo.deleteOnExit()
            val uri = FileProvider.getUriForFile(
                context,
                context.packageName + ".provider",
                archivo
            )
            imagenUri = uri
            takePictureLauncher.launch(uri)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        imagenUri?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = "Foto tomada",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(onClick = { permisoLauncher.launch(Manifest.permission.CAMERA) }) {
            Text("Tomar foto")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (imagenUri != null) {
            Button(onClick = {
                onFotoTomada(imagenUri!!)
            }) {
                Text("Confirmar foto")
            }
        }
    }
}
