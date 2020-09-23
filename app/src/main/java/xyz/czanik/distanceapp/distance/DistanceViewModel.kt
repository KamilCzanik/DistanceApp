package xyz.czanik.distanceapp.distance

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.Observables
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.subjects.BehaviorSubject
import xyz.czanik.distanceapp.UseCase
import xyz.czanik.distanceapp.distance.DistanceContract.Query
import xyz.czanik.distanceapp.distance.DistanceContract.Result
import xyz.czanik.distanceapp.distance.DistanceContract.SearchViewModel
import xyz.czanik.distanceapp.distance.DistanceContract.StationViewModel
import xyz.czanik.distanceapp.entities.Keyword
import xyz.czanik.distanceapp.entities.Station

class DistanceViewModel(
    getStationsUseCase: UseCase<GetStationsRequest, GetStationsResponse>,
    getKeywordsUseCase: UseCase<GetKeywordsRequest, GetKeywordsResponse>
) : ViewModel(), SearchViewModel {

    private val stationsToKeywordsSubject = BehaviorSubject.create<List<Pair<Station, Keyword>>>()
    private val disposables = CompositeDisposable()

    init {
        Observables.combineLatest(
            getStationsUseCase.invoke(GetStationsRequest).toObservable(),
            getKeywordsUseCase.invoke(GetKeywordsRequest).toObservable(),
            ::pairStationsWithKeywords
        )
                .retry()
                .subscribe(stationsToKeywordsSubject::onNext, ::handleError)
                .addTo(disposables)
    }

    override fun search(query: Query): Result? = stationsToKeywordsSubject.value
            ?.filter { it.second.value.contains(query.value, ignoreCase = true) }
            ?.map { StationViewModel(it.first.id.value, it.first.name.value) }
            ?.let(DistanceContract::Result)

    override fun onCleared() = disposables.dispose()

    private fun pairStationsWithKeywords(
        stationsResponse: GetStationsResponse,
        keywordsResponse: GetKeywordsResponse
    ): List<Pair<Station, Keyword>> = stationsResponse.stations.map { station ->
        station to keywordsResponse.keywords.first { it.stationId == station.id }
    }

    private fun handleError(error: Throwable) = Unit
}