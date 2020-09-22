package xyz.czanik.distanceapp

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import xyz.czanik.distanceapp.distance.KeywordsRepository
import xyz.czanik.distanceapp.distance.KoleoService
import xyz.czanik.distanceapp.distance.StationsRepository
import xyz.czanik.distanceapp.entities.Keyword
import xyz.czanik.distanceapp.entities.Station

class KoleoRepositoriesFactory : RepositoriesFactory {

    private val koleoService by lazy(::createRetrofit)

    override fun stations(): Repository<List<Station>> = StationsRepository(koleoService)

    override fun keywords(): Repository<List<Keyword>> = KeywordsRepository(koleoService)

    private fun createRetrofit() = Retrofit.Builder()
            .baseUrl("https://koleo.pl/api/v2/main/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
            .create(KoleoService::class.java)
}