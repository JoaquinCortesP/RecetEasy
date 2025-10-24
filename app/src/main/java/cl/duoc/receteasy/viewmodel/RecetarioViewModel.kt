package cl.duoc.receteasy.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.receteasy.model.Receta
import cl.duoc.receteasy.repository.RecetaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

data class EstadoUI(
    val nombreUsuario: String? = null,
    val recetas: List<Receta> = emptyList(),
    val consulta: String = "",
    val cargando: Boolean = false,
    val error: String? = null
)

class RecetarioViewModel(private val repositorio: RecetaRepository) : ViewModel() {

    private val _estadoUI = MutableStateFlow(EstadoUI())
    val estadoUI: StateFlow<EstadoUI> = _estadoUI.asStateFlow()

    init {
        viewModelScope.launch {
            repositorio.todasLasRecetas()
                .onStart { _estadoUI.value = _estadoUI.value.copy(cargando = true) }
                .catch { e -> _estadoUI.value = _estadoUI.value.copy(error = e.message, cargando = false) }
                .collect { lista -> _estadoUI.value = _estadoUI.value.copy(recetas = lista, cargando = false) }
        }
    }

    fun iniciarSesion(nombre: String) {
        _estadoUI.value = _estadoUI.value.copy(nombreUsuario = nombre)
    }

    fun cerrarSesion() {
        _estadoUI.value = _estadoUI.value.copy(nombreUsuario = null)
    }

    fun setConsulta(q: String) {
        _estadoUI.value = _estadoUI.value.copy(consulta = q)
        viewModelScope.launch {
            repositorio.buscar(q)
                .catch { /* no hacemos nada por simplicidad */ }
                .collect { lista -> _estadoUI.value = _estadoUI.value.copy(recetas = lista) }
        }
    }

    fun crearReceta(titulo: String, descripcion: String, ingredientesCsv: String, pasos: String, imagenUri: Uri?, creador: String?) {
        if (titulo.isBlank()) {
            _estadoUI.value = _estadoUI.value.copy(error = "El título no puede estar vacío")
            return
        }
        viewModelScope.launch {
            val receta = Receta(
                titulo = titulo.trim(),
                descripcion = descripcion.trim(),
                ingredientes = ingredientesCsv.trim(),
                pasos = pasos.trim(),
                imagenUri = imagenUri?.toString(),
                creador = creador
            )
            repositorio.insertar(receta)
            _estadoUI.value = _estadoUI.value.copy(error = null)
        }
    }

    suspend fun obtenerRecetaPorId(id: Long): Receta? {
        return repositorio.buscarPorId(id)
    }

    fun setError(mensaje: String) {
        _estadoUI.value = _estadoUI.value.copy(error = mensaje)
    }

    fun limpiarError() {
        _estadoUI.value = _estadoUI.value.copy(error = null)
    }
}
