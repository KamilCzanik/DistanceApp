package xyz.czanik.distanceapp

import io.reactivex.rxjava3.core.Single

interface UseCase<Request, Response> {
    fun invoke(request: Request): Single<Response>
}