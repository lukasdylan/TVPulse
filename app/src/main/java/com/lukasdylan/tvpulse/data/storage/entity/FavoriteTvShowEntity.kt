package com.lukasdylan.tvpulse.data.storage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_tv_show")
class FavoriteTvShowEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String,
    val genres: List<String>,
    val rating: Double,
    val createdDate: Long,
)