package xyz.czanik.distanceapp.distance

interface DistanceContract {

    data class StationViewModel(val stationId: Int, val name: String)

    interface SearchViewModel {

        fun search(query: Query): Result?

        data class Query(val value: String)

        data class Result(val value: List<StationViewModel>)
    }
}

