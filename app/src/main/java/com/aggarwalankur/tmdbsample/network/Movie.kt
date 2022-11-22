package com.aggarwalankur.tmdbsample.network

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Entity(tableName = "movies")
@Parcelize
data class Movie(

    @PrimaryKey(autoGenerate = true)
    val movie_key: Long,

    @field:SerializedName("id") val id: Long,
    @field:SerializedName("title") val title: String,
    @field:SerializedName("overview") val overview: String,

    @field:SerializedName("poster_path") val posterPath: String,
    @field:SerializedName("release_date") val releaseDate: String,
    @field:SerializedName("vote_average") val voteAverage: Double,
    @field:SerializedName("vote_count") val voteCount: Int,
    @field:SerializedName("popularity") val popularity: Double


) : Parcelable