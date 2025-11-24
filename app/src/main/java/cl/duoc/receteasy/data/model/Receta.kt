package cl.duoc.receteasy.data.model

import androidx.room.Entity
import cl.duoc.receteasy.data.model.Ingrediente
import androidx.room.PrimaryKey

@Entity(tableName = "recetas")
data class Receta(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titulo: String,
    val descripcion: String,
    val ingredientes: List<Ingrediente>,
    val pasos: String,
    val imagenUri: String? = "default_receta.png",
    val creador: String? = null,
    val creadaEn: Long = System.currentTimeMillis()
)
