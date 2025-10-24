package cl.duoc.receteasy.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cl.duoc.receteasy.model.Receta
import cl.duoc.receteasy.model.Usuario

@Database(entities = [Receta::class, Usuario::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class BaseDeDatos : RoomDatabase() {
    abstract fun recetaDao(): RecetaDao
    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile
        private var INSTANCIA: BaseDeDatos? = null

        fun obtenerInstancia(context: Context): BaseDeDatos =
            INSTANCIA ?: synchronized(this) {
                INSTANCIA ?: crearBase(context).also { INSTANCIA = it }
            }

        private fun crearBase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                BaseDeDatos::class.java,
                "recetario.db"
            )
                .fallbackToDestructiveMigration() // Esto borra la BD antigua y crea las tablas nuevas
                .build()
    }
}

