package com.lukasdylan.tvpulse.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
class TvShowRatingResponseBody(
    val average: Double? = null,
    val original: String? = null,
) {
}