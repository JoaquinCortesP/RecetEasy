package cl.duoc.receteasy.viewmodel

import cl.duoc.receteasy.data.model.Ingrediente
import cl.duoc.receteasy.data.model.Receta
import cl.duoc.receteasy.repository.RecetaRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class RecetarioViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repositorio: RecetaRepository = mockk(relaxed = true)

    private fun recetasVacias(): Flow<List<Receta>> = flowOf(emptyList())

    @Test
    fun agregarIngrediente_actualizaListaIngredientes() {
        runTest {
            every { repositorio.todasLasRecetas() } returns recetasVacias()

            val viewModel = RecetarioViewModel(repositorio)

            viewModel.agregarIngrediente(
                nombre = "Tomate",
                cantidad = 2.0,
                unidad = "unidades"
            )

            val lista = viewModel.ingredientesTemp

            assertEquals(1, lista.size)

            val ingrediente: Ingrediente = lista.first()
            assertEquals("tomate", ingrediente.nombre)
            assertEquals("unidades", ingrediente.unidad)
            assertEquals(2.0, ingrediente.cantidad, 0.0)
        }
    }
}
