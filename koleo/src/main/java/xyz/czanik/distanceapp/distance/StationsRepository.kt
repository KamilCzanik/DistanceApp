package xyz.czanik.distanceapp.distance

import io.reactivex.rxjava3.core.Single
import xyz.czanik.distanceapp.Repository
import xyz.czanik.distanceapp.entities.Location
import xyz.czanik.distanceapp.entities.Station

internal class StationsRepository(
    private val koleoService: KoleoService
) : Repository<List<Station>> {

    override fun get(): Single<List<Station>> = koleoService.allStations().map(::toDomainStations)

    private fun toDomainStations(stations: List<KoleoStation>): List<Station> = stations.map(::toDomainStation)

    private fun toDomainStation(it: KoleoStation): Station = Station(
        Station.Id(it.id),
        Station.Name(it.name),
        Location(it.latitude, it.longitude),
        it.hits
    )
}