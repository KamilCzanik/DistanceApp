package xyz.czanik.distanceapp.distance

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import xyz.czanik.distanceapp.Repository
import xyz.czanik.distanceapp.entities.Location
import xyz.czanik.distanceapp.entities.Station

internal class StationsRepository(
    private val koleoService: KoleoService
) : Repository<List<Station>> {

    override fun get(): Single<List<Station>> = koleoService
            .allStations()
            .subscribeOn(Schedulers.io())
            //.map { it.take(100) }
            .map(::toDomainStations)

    private fun toDomainStations(koleoStations: List<KoleoStation>): List<Station> = koleoStations.map(::toDomainStation)

    private fun toDomainStation(koleoStation: KoleoStation): Station = Station(
        Station.Id(koleoStation.id),
        Station.Name(koleoStation.name),
        Location(koleoStation.latitude, koleoStation.longitude),
        koleoStation.hits
    )
}