package xyz.czanik.distanceapp.distance

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Headers

internal interface KoleoService {

    @Headers("X-KOLEO-Version: 1")
    @GET("stations")
    fun allStations(): Single<List<KoleoStation>>

    @Headers("X-KOLEO-Version: 1")
    @GET("station_keywords")
    fun allKeywords(): Single<List<KoleoKeyword>>
}