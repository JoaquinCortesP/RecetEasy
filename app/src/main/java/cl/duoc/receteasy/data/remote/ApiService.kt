package cl.duoc.receteasy.data.remote

import cl.duoc.receteasy.data.model.Ingrediente
import retrofit2.http.GET

interface ApiService {
    @GET("ingrediente")
    suspend fun getIngredientes(): List<Ingrediente>
}
