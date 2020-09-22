package xyz.czanik.distanceapp.entities

data class Station(
    val id: Id,
    val name: Name,
    val location: Location,
    val hits: Int
) {

    data class Id(val value: String)

    data class Name(val value: String)
}
