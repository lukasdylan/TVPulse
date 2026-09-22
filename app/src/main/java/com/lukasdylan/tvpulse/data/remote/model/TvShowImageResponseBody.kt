package com.lukasdylan.tvpulse.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
class TvShowImageResponseBody(
    val medium: String? = null,
    val original: String? = null,
) {
}