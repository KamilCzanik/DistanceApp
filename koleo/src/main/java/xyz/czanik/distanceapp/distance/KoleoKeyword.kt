package xyz.czanik.distanceapp.distance

import com.google.gson.annotations.SerializedName

data class KoleoKeyword(
    @SerializedName("station_id") val stationId: Int,
    @SerializedName("keyword") val keyword: String
)