package cl.duoc.receteasy.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recetas")
data class Receta(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titulo: String,
    val descripcion: String,
    val ingredientes: String,
    val pasos: String,
    val imagenUri: String? = null,
    val creador: String? = null,
    val creadaEn: Long = System.currentTimeMillis()
)


