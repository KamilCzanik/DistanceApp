package xyz.czanik.distanceapp.entities

data class Distance(val value: Double, val unit: DistanceUnit)

enum class DistanceUnit {
    Meter,
    Kilometer
}