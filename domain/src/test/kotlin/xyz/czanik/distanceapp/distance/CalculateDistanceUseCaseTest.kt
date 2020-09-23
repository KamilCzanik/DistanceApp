package xyz.czanik.distanceapp.distance

import io.reactivex.rxjava3.core.Single
import org.junit.Test
import xyz.czanik.distanceapp.entities.Distance
import xyz.czanik.distanceapp.entities.DistanceUnit
import xyz.czanik.distanceapp.entities.Location

class CalculateDistanceUseCaseTest {

    private val useCase = CalculateDistanceUseCase()

    @Test
    fun `test useCase calculates distance when both points have same coordinates`() {
        val request = CalculateDistanceRequest(Location(0.0, 0.0), Location(0.0, 0.0))
        useCase.invoke(request).assertDistance(0.0)
    }

    @Test
    fun `test useCase calculates coordinates correctly`() {
        val request = CalculateDistanceRequest(Location(0.0, 0.0), Location(1.0, 1.0))
        useCase.invoke(request).assertDistance(157249.6280925079)
    }

    private fun Single<CalculateDistanceResponse>.assertDistance(expected: Double) = test().assertResult(
        CalculateDistanceResponse(Distance(expected, DistanceUnit.Meter))
    )
}