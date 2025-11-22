package cl.duoc.receteasy.repository

import cl.duoc.receteasy.data.model.Receta
import kotlinx.coroutines.flow.Flow

class RecetaRepository(private val dao: RecetaDao) {

    fun todasLasRecetas(): Flow<List<Receta>> = dao.obtenerTodas()

    fun buscar(query: String): Flow<List<Receta>> = dao.buscar(query)


    suspend fun buscarPorId(id: Long): Receta? = dao.buscarPorId(id)

    suspend fun insertar(receta: Receta): Long = dao.insertar(receta)

    suspend fun eliminar(receta: Receta) = dao.eliminar(receta)
}

