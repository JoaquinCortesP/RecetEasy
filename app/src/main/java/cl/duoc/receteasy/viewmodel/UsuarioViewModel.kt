package cl.duoc.receteasy.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.receteasy.data.model.Usuario
import cl.duoc.receteasy.repository.BaseDeDatos
import cl.duoc.receteasy.session.SessionManager
import kotlinx.coroutines.launch

class UsuarioViewModel(application: Application) : AndroidViewModel(application) {

    private val db = BaseDeDatos.obtenerInstancia(application)
    private val usuarioDao = db.usuarioDao()

    private val session = SessionManager(application)

    // Registrar usuario
    fun registrarUsuario(nombre: String, contrasena: String, fotoUri: Uri?, callback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val existente = usuarioDao.obtenerPorNombre(nombre)
            if (existente != null) {
                callback(false, "El usuario ya existe")
                return@launch
            }

            val usuario = Usuario(
                nombre = nombre,
                contrasena = contrasena,
                correo = "",
                fotoUri = fotoUri?.toString()
            )

            usuarioDao.insertar(usuario)
            callback(true, "Usuario registrado correctamente")
        }
    }

    // Iniciar sesión
    fun iniciarSesion(nombre: String, contrasena: String, callback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val usuario = usuarioDao.obtenerPorNombreYContrasena(nombre, contrasena)
            if (usuario == null) {
                callback(false, "Usuario o contraseña incorrectos")
            } else {
                // Guarda sesión persistente
                session.guardarSesion(usuario.nombre, usuario.fotoUri)
                callback(true, "Bienvenido ${usuario.nombre}")
            }
        }
    }

    // Obtener usuario logueado actualmente
    fun obtenerUsuarioLogueado(): Usuario? {
        val nombre = session.obtenerUsuario() ?: return null
        val foto = session.obtenerFoto()

        return Usuario(
            nombre = nombre,
            contrasena = "",
            correo = "",
            fotoUri = foto
        )
    }

    fun cerrarSesion() {
        session.cerrarSesion()
    }
}
