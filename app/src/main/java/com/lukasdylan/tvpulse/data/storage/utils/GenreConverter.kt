package com.lukasdylan.tvpulse.data.storage.utils

import androidx.room.TypeConverter

class GenreConverter {
    @TypeConverter
    fun fromGenreList(genres: List<String>): String =
        genres.joinToString(separator = GENRE_SEPARATOR)

    @TypeConverter
    fun toGenreList(data: String): List<String> =
        if (data.isEmpty()) emptyList() else data.split(GENRE_SEPARATOR)

    private companion object {
        const val GENRE_SEPARATOR = "||"
    }
}