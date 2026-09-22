package com.lukasdylan.tvpulse.data.remote.service

import com.lukasdylan.tvpulse.data.remote.model.SearchTvShowResponseBody
import com.lukasdylan.tvpulse.data.remote.model.TvShowResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TvMazeApiService {

    @GET("shows")
    suspend fun getTvShows(): List<TvShowResponseBody>

    @GET("search/shows")
    suspend fun searchTvShows(@Query("q") query: String): List<SearchTvShowResponseBody>

    @GET("shows/{id}")
    suspend fun getTvShowDetail(@Path("id") id: Int): TvShowResponseBody
}
