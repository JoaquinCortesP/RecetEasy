package cl.duoc.receteasy.data.remote

import com.google.gson.annotations.SerializedName

data class IngredienteRemote(
    val id: Int,
    @SerializedName("name")
    val nombre: String,
    @SerializedName("created_at")
    val createdAt: Long
)
