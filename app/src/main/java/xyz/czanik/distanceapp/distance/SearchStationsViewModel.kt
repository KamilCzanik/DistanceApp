package xyz.czanik.distanceapp.distance

import xyz.czanik.distanceapp.distance.DistanceContract.SearchViewModel
import xyz.czanik.distanceapp.distance.DistanceContract.SearchViewModel.Query
import xyz.czanik.distanceapp.distance.DistanceContract.SearchViewModel.Result
import xyz.czanik.distanceapp.distance.DistanceContract.StationViewModel

class SearchStationsViewModel(
    private val source: DistanceContract.StationSearchablesSource
) : SearchViewModel {

    override fun search(query: Query): Result? = source.stationSearchablesValue
            ?.filter { it.keyword.contains(query.value, ignoreCase = true) }
            ?.sortedByDescending { it.station.hits }
            ?.map { StationViewModel(it.station.id.value, it.station.name.value) }
            ?.let(SearchViewModel::Result)
}