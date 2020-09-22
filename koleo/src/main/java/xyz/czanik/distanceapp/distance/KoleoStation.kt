package xyz.czanik.distanceapp.distance

import com.google.gson.annotations.SerializedName

internal data class KoleoStation(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("latitude") val latitude: Float,
    @SerializedName("longitude") val longitude: Float,
    @SerializedName("hits") val hits: Int
)