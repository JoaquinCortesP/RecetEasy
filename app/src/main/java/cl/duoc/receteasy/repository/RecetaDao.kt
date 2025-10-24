package cl.duoc.receteasy.repository

import androidx.room.*
import cl.duoc.receteasy.model.Receta
import kotlinx.coroutines.flow.Flow

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

    @Query("SELECT * FROM recetas WHERE titulo LIKE '%' || :q || '%' OR ingredientes LIKE '%' || :q || '%' ORDER BY creadaEn DESC")
    fun buscar(q: String): Flow<List<Receta>>
}

