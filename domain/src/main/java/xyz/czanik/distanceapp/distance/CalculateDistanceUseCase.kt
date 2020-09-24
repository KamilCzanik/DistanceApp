package xyz.czanik.distanceapp.distance

import com.grum.geocalc.Coordinate
import com.grum.geocalc.EarthCalc
import com.grum.geocalc.Point
import io.reactivex.rxjava3.core.Single
import xyz.czanik.distanceapp.UseCase
import xyz.czanik.distanceapp.entities.Distance
import xyz.czanik.distanceapp.entities.DistanceUnit
import xyz.czanik.distanceapp.entities.Location

data class CalculateDistanceRequest(val from: Location, val to: Location)

data class CalculateDistanceResponse(val distance: Distance)

class CalculateDistanceUseCase : UseCase<CalculateDistanceRequest, CalculateDistanceResponse> {

    override fun invoke(request: CalculateDistanceRequest): Single<CalculateDistanceResponse> = Single.just(
        CalculateDistanceResponse(distance(request.from, request.to))
    )

    private fun distance(from: Location, to: Location) = Distance(
        EarthCalc.harvesineDistance(from.asPoint(), to.asPoint()),
        DistanceUnit.Meter
    )

    private fun Location.asPoint() = Point.at(Coordinate.fromDegrees(lat), Coordinate.fromDegrees(lng))
}