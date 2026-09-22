package com.lukasdylan.tvpulse.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
class TvShowResponseBody(
    val id: Int?,
    val name: String?,
    val summary: String?,
    val genres: List<String>?,
    val image: TvShowImageResponseBody?,
    val rating: TvShowRatingResponseBody?,
)
