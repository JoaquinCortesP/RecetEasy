package cl.duoc.receteasy.viewmodel

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.receteasy.model.Ingrediente
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

    private val _ingredientesTemp = mutableStateListOf<Ingrediente>()
    val ingredientesTemp: List<Ingrediente> get() = _ingredientesTemp

    private var recetasTotales: List<Receta> = emptyList()

    init {
        viewModelScope.launch {
            repositorio.todasLasRecetas()
                .onStart {
                    _estadoUI.value = _estadoUI.value.copy(cargando = true)
                }
                .catch { e ->
                    _estadoUI.value = _estadoUI.value.copy(error = e.message, cargando = false)
                }
                .collect { lista ->
                    recetasTotales = lista
                    _estadoUI.value = _estadoUI.value.copy(
                        recetas = lista,
                        cargando = false
                    )
                }
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
        filtrarRecetas(q)
    }

    private fun filtrarRecetas(query: String) {
        val texto = query.trim()

        if (texto.isBlank()) {
            _estadoUI.value = _estadoUI.value.copy(recetas = recetasTotales)
            return
        }

        val ingredientesBuscados = texto.lowercase()
            .split(",", " ", ";")
            .map { it.trim() }
            .filter { it.isNotBlank() }

        val filtradas = recetasTotales.filter { receta ->
            val ing = receta.ingredientes.map { it.nombre.lowercase() }
            ingredientesBuscados.all { buscado ->
                ing.any { it.contains(buscado) }
            }
        }

        _estadoUI.value = _estadoUI.value.copy(recetas = filtradas)
    }

    /**
     * Devuelve true si la receta se creó correctamente (para que la UI sepa si puede navegar).
     */
    fun crearReceta(
        titulo: String,
        descripcion: String,
        pasos: String,
        imagenUri: Uri?,
        creador: String?
    ): Boolean {
        if (titulo.isBlank()) {
            setError("El título no puede estar vacío.")
            return false
        }
        if (_ingredientesTemp.isEmpty()) {
            setError("Debe agregar al menos un ingrediente.")
            return false
        }

        limpiarError()

        viewModelScope.launch {
            val receta = Receta(
                titulo = titulo.trim(),
                descripcion = descripcion.trim(),
                ingredientes = _ingredientesTemp.toList(),
                pasos = pasos.trim(),
                imagenUri = imagenUri?.toString() ?: "default_receta.png",
                creador = creador
            )

            repositorio.insertar(receta)
            limpiarIngredientesTemp()
            // El Flow del init se encargará de refrescar recetasTotales + estadoUI
        }

        return true
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

    fun agregarIngrediente(nombre: String, cantidad: Double, unidad: String) {
        val limpio = nombre.trim().lowercase()
        if (_ingredientesTemp.any { it.nombre == limpio }) return

        _ingredientesTemp.add(
            Ingrediente(
                nombre = limpio,
                cantidad = cantidad,
                unidad = unidad
            )
        )
    }

    fun limpiarIngredientesTemp() {
        _ingredientesTemp.clear()
    }
}
