package xyz.czanik.distanceapp.distance

import xyz.czanik.distanceapp.entities.Station

interface DistanceContract {

    data class StationViewModel(val stationId: Station.Id, val name: String)

    data class Query(val value: String)

    data class Result(val value: List<StationViewModel>)

    interface SearchViewModel {

        fun search(query: Query): Result?
    }
}

