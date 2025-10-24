package cl.duoc.receteasy.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cl.duoc.receteasy.model.Usuario

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(usuario: Usuario)

    @Query("SELECT * FROM usuarios WHERE nombre = :nombre LIMIT 1")
    suspend fun obtenerPorNombre(nombre: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE nombre = :nombre AND contrasena = :contrasena LIMIT 1")
    suspend fun obtenerPorNombreYContrasena(nombre: String, contrasena: String): Usuario?

    @Query("SELECT * FROM usuarios")
    suspend fun obtenerTodos(): List<Usuario>
}
