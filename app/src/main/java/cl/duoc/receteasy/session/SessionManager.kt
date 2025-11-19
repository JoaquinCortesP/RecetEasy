package cl.duoc.receteasy.session

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("sesion", Context.MODE_PRIVATE)

    fun guardarSesion(nombre: String, fotoUri: String?) {
        prefs.edit()
            .putString("usuario_nombre", nombre)
            .putString("usuario_foto", fotoUri)
            .apply()
    }

    fun obtenerUsuario(): String? = prefs.getString("usuario_nombre", null)

    fun obtenerFoto(): String? = prefs.getString("usuario_foto", null)

    fun sesionActiva(): Boolean = prefs.contains("usuario_nombre")

    fun cerrarSesion() {
        prefs.edit().clear().apply()
    }
}
