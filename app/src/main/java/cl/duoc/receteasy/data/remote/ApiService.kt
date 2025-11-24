package cl.duoc.receteasy.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("ingrediente")
    suspend fun obtenerIngredientes(): List<IngredienteRemote>

}
