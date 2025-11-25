package cl.duoc.receteasy.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

class IngredienteTest {

    @Test
    fun crearIngrediente_guardaDatosCorrectamente() {
        val ingrediente = Ingrediente(
            id = 1L,
            nombre = "Tomate",
            unidad = "unidades",
            cantidad = 2.0
        )

        assertEquals(1L, ingrediente.id)
        assertEquals("Tomate", ingrediente.nombre)
        assertEquals("unidades", ingrediente.unidad)
        assertEquals(2.0, ingrediente.cantidad, 0.0)
    }

    @Test
    fun copyIngrediente_modificaSoloCantidad() {
        val ingrediente = Ingrediente(
            id = 1L,
            nombre = "Tomate",
            unidad = "unidades",
            cantidad = 2.0
        )

        val modificado = ingrediente.copy(cantidad = 3.0)

        assertEquals(1L, modificado.id)
        assertEquals("Tomate", modificado.nombre)
        assertEquals("unidades", modificado.unidad)
        assertEquals(3.0, modificado.cantidad, 0.0)
    }
}
