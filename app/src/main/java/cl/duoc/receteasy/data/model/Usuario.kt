package cl.duoc.receteasy.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val contrasena: String,
    val correo: String,
    val fotoUri: String? = "default_user.png"
)

