package cl.duoc.receteasy.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.receteasy.data.remote.IngredienteRemote
import cl.duoc.receteasy.data.remote.RetrofitInstance
import kotlinx.coroutines.launch

data class IngredientesRemotosUiState(
    val cargando: Boolean = false,
    val ingredientes: List<IngredienteRemote> = emptyList(),
    val error: String? = null
)

class IngredientesRemoteViewModel : ViewModel() {

    var uiState by mutableStateOf(IngredientesRemotosUiState())
        private set

    init {
        // Cargar automáticamente al entrar a la pantalla
        cargarIngredientes()
    }

    fun cargarIngredientes() {
        uiState = uiState.copy(cargando = true, error = null)

        viewModelScope.launch {
            try {
                val lista = RetrofitInstance.api.obtenerIngredientes()
                uiState = uiState.copy(
                    cargando = false,
                    ingredientes = lista,
                    error = null
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    cargando = false,
                    error = "Error al cargar ingredientes: ${e.message ?: "desconocido"}"
                )
            }
        }
    }
}
