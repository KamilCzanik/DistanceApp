package xyz.czanik.distanceapp.distance

import androidx.lifecycle.LiveData
import xyz.czanik.distanceapp.entities.Station

interface DistanceContract {

    data class StationViewModel(val stationId: Int, val name: String)

    interface SearchViewModel {

        fun search(query: Query): Result?

        data class Query(val value: String)

        data class Result(val value: List<StationViewModel>)
    }

    interface CalculateDistanceViewModel {

        val distance: LiveData<DistanceViewModel>

        fun startStationSelected(id: Station.Id?)

        fun endStationSelected(id: Station.Id?)

        data class DistanceViewModel(val distanceInKilometers: Float?)
    }
}

