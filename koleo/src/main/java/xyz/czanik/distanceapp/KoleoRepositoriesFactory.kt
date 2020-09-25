package xyz.czanik.distanceapp

import android.content.Context
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import xyz.czanik.distanceapp.distance.KeywordsRepository
import xyz.czanik.distanceapp.distance.KoleoService
import xyz.czanik.distanceapp.distance.StationsRepository
import xyz.czanik.distanceapp.entities.Keyword
import xyz.czanik.distanceapp.entities.Station
import java.io.File

class KoleoRepositoriesFactory(private val appContext: Context) : RepositoriesFactory {

    private val koleoService by lazy(::createKoleoService)

    override fun stations(): Repository<List<Station>> = StationsRepository(koleoService)

    override fun keywords(): Repository<List<Keyword>> = KeywordsRepository(koleoService)

    private fun createKoleoService() = Retrofit.Builder()
            .client(buildHttpClient())
            .baseUrl("https://koleo.pl/api/v2/main/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
            .create(KoleoService::class.java)

    private fun buildHttpClient(): OkHttpClient {
        val cache = Cache(File(appContext.cacheDir, "koleo_cache"), CACHE_FILE_SIZE)
        return OkHttpClient.Builder()
                .cache(cache)
                .addNetworkInterceptor(CacheInterceptor(appContext))
                .build()
    }

    companion object {
        const val CACHE_FILE_SIZE = 5L * 1024 * 1024
    }
}