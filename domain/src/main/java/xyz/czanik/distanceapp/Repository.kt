package xyz.czanik.distanceapp

import io.reactivex.rxjava3.core.Single

interface Repository<Data> {

    fun get(): Single<Data>
}