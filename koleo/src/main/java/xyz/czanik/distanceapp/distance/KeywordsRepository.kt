package xyz.czanik.distanceapp.distance

import io.reactivex.rxjava3.core.Single
import xyz.czanik.distanceapp.Repository
import xyz.czanik.distanceapp.entities.Keyword
import xyz.czanik.distanceapp.entities.Station

internal class KeywordsRepository(
    private val koleoService: KoleoService
) : Repository<List<Keyword>> {

    override fun get(): Single<List<Keyword>> = koleoService.allKeywords().map(::toDomainKeywords)

    private fun toDomainKeywords(koleoKeywords: List<KoleoKeyword>): List<Keyword> = koleoKeywords.map(::toDomainKeyword)

    private fun toDomainKeyword(koleoKeyword: KoleoKeyword): Keyword = Keyword(Station.Id(koleoKeyword.stationId), koleoKeyword.keyword)
}