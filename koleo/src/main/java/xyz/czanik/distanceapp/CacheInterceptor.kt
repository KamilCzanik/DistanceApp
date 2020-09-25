package xyz.czanik.distanceapp

import android.content.Context
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

class CacheInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.proceed(chain.request())
            .newBuilder()
            .header("Cache-Control", cacheControl().toString())
            .build()

    private fun cacheControl() = if (hasNetwork(context))
        CacheControl.Builder().maxAge(CACHE_MAX_TIME_IN_DAYS, TimeUnit.DAYS).build()
    else
        CacheControl.Builder().onlyIfCached().maxStale(CACHE_MAX_TIME_IN_DAYS, TimeUnit.DAYS).build()

    companion object {
        private const val CACHE_MAX_TIME_IN_DAYS = 1
    }
}