package xyz.czanik.distanceapp.distance

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.Observables
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import xyz.czanik.distanceapp.UseCase
import xyz.czanik.distanceapp.distance.DistanceContract.CalculateDistanceViewModel
import xyz.czanik.distanceapp.distance.DistanceContract.CalculateDistanceViewModel.DistanceViewModel
import xyz.czanik.distanceapp.distance.DistanceContract.SearchViewModel
import xyz.czanik.distanceapp.distance.DistanceContract.SearchViewModel.Query
import xyz.czanik.distanceapp.distance.DistanceContract.SearchViewModel.Result
import xyz.czanik.distanceapp.distance.DistanceContract.StationViewModel
import xyz.czanik.distanceapp.entities.Distance
import xyz.czanik.distanceapp.entities.DistanceUnit
import xyz.czanik.distanceapp.entities.Keyword
import xyz.czanik.distanceapp.entities.Station
import xyz.czanik.distanceapp.util.Optional

class StationsDistanceViewModel(
    getStationsUseCase: UseCase<GetStationsRequest, GetStationsResponse>,
    getKeywordsUseCase: UseCase<GetKeywordsRequest, GetKeywordsResponse>,
    private val calculateDistanceUseCase: UseCase<CalculateDistanceRequest, CalculateDistanceResponse>
) : ViewModel(), SearchViewModel, CalculateDistanceViewModel {

    private val stationsToKeywordsSubject = BehaviorSubject.create<List<Pair<Station, Keyword>>>()
    private val startStation = PublishSubject.create<Optional<Station.Id>>()
    private val endStation = PublishSubject.create<Optional<Station.Id>>()
    private val disposables = CompositeDisposable()
    override val distance = MutableLiveData<DistanceViewModel>(DistanceViewModel(null))

    init {
        Observables.combineLatest(
            getStationsUseCase.invoke(GetStationsRequest).toObservable(),
            getKeywordsUseCase.invoke(GetKeywordsRequest).toObservable(),
            ::pairStationsWithKeywords
        )
                .retry()
                .subscribe(stationsToKeywordsSubject::onNext, ::handleError)
                .addTo(disposables)
        Observables.combineLatest(
            startStation.distinctUntilChanged(),
            endStation.distinctUntilChanged(),
            stationsToKeywordsSubject
        ).switchMapSingle { (optionalStartId, optionalEndId, stationsToKeywords) ->
            if (optionalStartId.hasValue().not() || optionalEndId.hasValue().not())
                Single.just(DistanceViewModel(null))
            else
                calculateDistance(stationsToKeywords, optionalStartId, optionalEndId)
        }
                .subscribe(distance::postValue, ::handleError)
                .addTo(disposables)
    }

    private fun calculateDistance(
        stationsToKeywords: List<Pair<Station, Keyword>>,
        optionalStartId: Optional<Station.Id>,
        optionalEndId: Optional<Station.Id>
    ) = calculateDistanceUseCase.invoke(calculateDistanceRequest(stationsToKeywords, optionalStartId, optionalEndId))
            .map(::toDistanceViewModel)

    private fun toDistanceViewModel(response: CalculateDistanceResponse) = DistanceViewModel(response.distance.asKilometers())

    private fun Distance.asKilometers() = if (unit == DistanceUnit.Kilometer) value.toFloat() else value.toFloat() / 1000

    private fun calculateDistanceRequest(
        stationsToKeywords: List<Pair<Station, Keyword>>,
        optionalStartId: Optional<Station.Id>,
        optionalEndId: Optional<Station.Id>
    ) = CalculateDistanceRequest(
        stationsToKeywords.locationFor(optionalStartId.requireValue()),
        stationsToKeywords.locationFor(optionalEndId.requireValue())
    )

    private fun List<Pair<Station, Keyword>>.locationFor(startId: Station.Id) = first { it.first.id == startId }.first.location

    override fun search(query: Query): Result? = stationsToKeywordsSubject.value
            ?.filter { it.second.value.contains(query.value, ignoreCase = true) }
            ?.sortedBy { it.first.hits }
            ?.map { StationViewModel(it.first.id.value, it.first.name.value) }
            ?.let(SearchViewModel::Result)

    override fun onCleared() = disposables.dispose()

    override fun startStationSelected(id: Station.Id?) = startStation.onNext(Optional.ofNullable(id))

    override fun endStationSelected(id: Station.Id?) = endStation.onNext(Optional.ofNullable(id))

    private fun pairStationsWithKeywords(
        stationsResponse: GetStationsResponse,
        keywordsResponse: GetKeywordsResponse
    ): List<Pair<Station, Keyword>> = stationsResponse.stations.map { station ->
        station to keywordsResponse.keywords.first { it.stationId == station.id }
    }

    private fun handleError(error: Throwable) = println("PrintingError $error")
}