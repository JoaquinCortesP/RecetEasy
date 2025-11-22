package cl.duoc.receteasy.repository

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import cl.duoc.receteasy.data.model.Ingrediente

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun listaIngredientesAJson(list: List<Ingrediente>?): String? {
        return list?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun jsonAListaIngredientes(value: String?): List<Ingrediente> {
        if (value.isNullOrBlank()) return emptyList()
        val type = object : TypeToken<List<Ingrediente>>() {}.type
        return gson.fromJson(value, type)
    }
}
