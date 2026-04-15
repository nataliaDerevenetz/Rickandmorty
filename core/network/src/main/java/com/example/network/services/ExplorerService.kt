package com.example.network.di

import com.example.network.models.EpisodeCharacterResponse
import com.example.network.models.EpisodeInfoResponse
import com.example.network.models.EpisodesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ExplorerService {
    @GET("character/")
    suspend fun getCharacterSearchPaging(@Query("name") name:String,
                                         @Query("page") page:Int): Response<CharactersResponse>

    @GET("episode/")
    suspend fun getEpisodesPaging(@Query("page") page:Int): Response<EpisodesResponse>

    @GET("episode/{id}")
    suspend fun getEpisodeInfoById(@Path("id") id: Int): Response<EpisodeInfoResponse>

    @GET("character/{ids}")
    suspend fun getCharacterByIds(@Path("ids") id: String): Response<List<EpisodeCharacterResponse>>

    @GET("character/{id}")
    suspend fun getCharacterById(@Path("id") id: Int): Response<CharacterResponse>

}