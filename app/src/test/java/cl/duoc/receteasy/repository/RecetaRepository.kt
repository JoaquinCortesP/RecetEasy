package cl.duoc.receteasy.repository

import cl.duoc.receteasy.data.model.Receta
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RecetaRepositoryTest {

    private val dao: RecetaDao = mockk(relaxed = true)
    private val repository = RecetaRepository(dao)

    @Test
    fun todasLasRecetas_delegaEnDao() {
        runTest {
            val receta: Receta = mockk(relaxed = true)
            val recetas = listOf(receta)

            every { dao.obtenerTodas() } returns flowOf(recetas)

            val resultado = repository.todasLasRecetas().first()

            assertEquals(recetas, resultado)
        }
    }

    @Test
    fun insertar_delegaEnDao() {
        runTest {
            val receta: Receta = mockk(relaxed = true)

            repository.insertar(receta)

            coVerify { dao.insertar(receta) }
        }
    }

    @Test
    fun buscar_delegaEnDao() {
        runTest {
            val receta: Receta = mockk(relaxed = true)
            val recetas = listOf(receta)

            every { dao.buscar("pollo") } returns flowOf(recetas)

            val resultado = repository.buscar("pollo").first()

            assertEquals(recetas, resultado)
        }
    }
}
