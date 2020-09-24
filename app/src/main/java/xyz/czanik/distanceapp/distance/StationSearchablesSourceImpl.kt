package xyz.czanik.distanceapp.distance

import io.reactivex.rxjava3.kotlin.Observables
import io.reactivex.rxjava3.subjects.BehaviorSubject
import xyz.czanik.distanceapp.UseCase
import xyz.czanik.distanceapp.distance.DistanceContract.StationSearchablesSource.StationSearchable
import xyz.czanik.distanceapp.entities.Station

class StationSearchablesSourceImpl(
    getStationsUseCase: UseCase<GetStationsRequest, GetStationsResponse>,
    getKeywordsUseCase: UseCase<GetKeywordsRequest, GetKeywordsResponse>
) : RxViewModel(),
    DistanceContract.StationSearchablesSource {

    override val stationSearchables: BehaviorSubject<List<StationSearchable>> = BehaviorSubject.create()
    override val stationSearchablesValue: List<StationSearchable>? get() = stationSearchables.value

    init {
        Observables.combineLatest(
            getStationsUseCase.invoke(GetStationsRequest).toObservable(),
            getKeywordsUseCase.invoke(GetKeywordsRequest).toObservable(),
            ::toStationSearchables
        )
                .retry()
                .subscribe(stationSearchables::onNext, ::handleError)
                .manage()
    }

    private fun toStationSearchables(
        stationsResponse: GetStationsResponse,
        keywordsResponse: GetKeywordsResponse
    ): List<StationSearchable> = stationsResponse.stations.map { station ->
        StationSearchable(station, keywordValueForStationId(keywordsResponse, station))
    }

    private fun keywordValueForStationId(
        keywordsResponse: GetKeywordsResponse,
        station: Station
    ) = keywordsResponse.keywords.first { it.stationId == station.id }.value
}