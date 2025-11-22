package cl.duoc.restapp.data.remote

import cl.duoc.receteasy.data.model.Ingrediente
import retrofit2.http.GET

interface ApiService {
    @GET("posts")
    suspend fun getPosts(): List<Ingrediente>
}