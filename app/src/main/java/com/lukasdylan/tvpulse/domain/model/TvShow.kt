package com.lukasdylan.tvpulse.domain.model

data class TvShow(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val genres: List<String>,
    val rating: Double,
)
