package com.lukasdylan.tvpulse.domain.model

data class TvShowDetail(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val genres: List<String>,
    val rating: Double,
    val synopsis: String
)
