package xyz.czanik.distanceapp.distance

import io.reactivex.rxjava3.kotlin.Observables
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.BehaviorSubject
import xyz.czanik.distanceapp.UseCase
import xyz.czanik.distanceapp.distance.DistanceContract.StationSearchablesSource.StationSearchable
import xyz.czanik.distanceapp.entities.Station
import java.util.concurrent.TimeUnit.SECONDS

class StationSearchablesSourceImpl(
    private val getStationsUseCase: UseCase<GetStationsRequest, GetStationsResponse>,
    private val getKeywordsUseCase: UseCase<GetKeywordsRequest, GetKeywordsResponse>
) : RxViewModel(), DistanceContract.StationSearchablesSource {

    override val stationSearchables: BehaviorSubject<List<StationSearchable>> = BehaviorSubject.create()
    override val stationSearchablesValue: List<StationSearchable>? get() = stationSearchables.value

    init {
        Observables.combineLatest(
            stationsStream(),
            keywordsStream(),
            ::toStationSearchables
        )
                .subscribeBy(onNext = stationSearchables::onNext, onError = ::handleError)
                .manage()
    }

    private fun stationsStream() = getStationsUseCase.invoke(GetStationsRequest)
            .retryWithDelay(RETRY_FETCH_INTERVAL_IN_SECONDS, SECONDS)
            .toObservable()

    private fun keywordsStream() = getKeywordsUseCase.invoke(GetKeywordsRequest)
            .retryWithDelay(RETRY_FETCH_INTERVAL_IN_SECONDS, SECONDS)
            .toObservable()

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

    companion object {
        const val RETRY_FETCH_INTERVAL_IN_SECONDS = 1L
    }
}