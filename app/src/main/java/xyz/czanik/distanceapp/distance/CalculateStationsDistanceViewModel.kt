package xyz.czanik.distanceapp.distance

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.Observables
import io.reactivex.rxjava3.subjects.PublishSubject
import xyz.czanik.distanceapp.UseCase
import xyz.czanik.distanceapp.distance.DistanceContract.CalculateDistanceViewModel.DistanceViewModel
import xyz.czanik.distanceapp.distance.DistanceContract.StationSearchablesSource.StationSearchable
import xyz.czanik.distanceapp.entities.Distance
import xyz.czanik.distanceapp.entities.DistanceUnit
import xyz.czanik.distanceapp.entities.Station
import xyz.czanik.distanceapp.util.Optional

class CalculateStationsDistanceViewModel(
    private val source: DistanceContract.StationSearchablesSource,
    private val calculateDistanceUseCase: UseCase<CalculateDistanceRequest, CalculateDistanceResponse>
) : DistanceContract.CalculateDistanceViewModel {

    private val startStation = PublishSubject.create<Optional<Station.Id>>()
    private val endStation = PublishSubject.create<Optional<Station.Id>>()
    override val distance = distanceStream().toLiveData()

    override fun startStationSelected(id: Station.Id?) = startStation.onNext(Optional.ofNullable(id))

    override fun endStationSelected(id: Station.Id?) = endStation.onNext(Optional.ofNullable(id))

    private fun distanceStream() = Observables.combineLatest(
        startStation.distinctUntilChanged(),
        endStation.distinctUntilChanged(),
        source.stationSearchables
    ).switchMapSingle { (optionalStartId, optionalEndId, stationSearchables) ->
        calculateDistance(optionalStartId, optionalEndId, stationSearchables)
    }.startWithItem(DistanceViewModel(null))

    private fun calculateDistance(
        optionalStartId: Optional<Station.Id>,
        optionalEndId: Optional<Station.Id>,
        stationsToKeywords: List<StationSearchable>
    ) = if (optionalStartId.hasValue().not() || optionalEndId.hasValue().not())
        Single.just(DistanceViewModel(null))
    else
        calculateDistanceUseCase.invoke(stationsToKeywords.asRequest(optionalStartId, optionalEndId))
                .map(::toDistanceViewModel)

    private fun List<StationSearchable>.asRequest(
        optionalStartId: Optional<Station.Id>,
        optionalEndId: Optional<Station.Id>
    ) = CalculateDistanceRequest(
        locationFor(optionalStartId.requireValue()),
        locationFor(optionalEndId.requireValue())
    )

    private fun toDistanceViewModel(response: CalculateDistanceResponse) = DistanceViewModel(response.distance.asKilometers())

    private fun Distance.asKilometers() = if (unit == DistanceUnit.Kilometer) value.toFloat() else value.toFloat() / 1000

    private fun List<StationSearchable>.locationFor(startId: Station.Id) = first { it.station.id == startId }.station.location
}

fun <T> Observable<T>.toLiveData(): LiveData<T> {
    return LiveDataReactiveStreams.fromPublisher(toFlowable(BackpressureStrategy.LATEST))
}