package cl.duoc.receteasy.repository

import androidx.room.*
import cl.duoc.receteasy.data.model.Ingrediente
import cl.duoc.receteasy.data.model.Receta
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
interface RecetaDao {

    @Query("SELECT * FROM recetas ORDER BY creadaEn DESC")
    fun obtenerTodas(): Flow<List<Receta>>

    @Query("SELECT * FROM recetas WHERE id = :id")
    suspend fun buscarPorId(id: Long): Receta?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(receta: Receta): Long

    @Delete
    suspend fun eliminar(receta: Receta)


    fun buscar(query: String): Flow<List<Receta>> = obtenerTodas().map { recetas ->
        val buscados = query.split(",")
            .map { it.trim().lowercase() }
            .filter { it.isNotBlank() }

        recetas.filter { receta ->
            buscados.all { busq ->
                receta.ingredientes.any { ing ->
                    (ing.nombre ?: "")
                        .lowercase()
                        .contains(busq)
                }
            }
        }
    }



}

