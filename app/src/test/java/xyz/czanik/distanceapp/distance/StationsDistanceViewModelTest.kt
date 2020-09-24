package xyz.czanik.distanceapp.distance

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import xyz.czanik.distanceapp.UseCase
import xyz.czanik.distanceapp.distance.DistanceContract.CalculateDistanceViewModel.DistanceViewModel
import xyz.czanik.distanceapp.entities.Distance
import xyz.czanik.distanceapp.entities.DistanceUnit
import xyz.czanik.distanceapp.entities.Location
import xyz.czanik.distanceapp.entities.Station

class CalculateStationsDistanceViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()
    private val searchables = listOf(
        DistanceContract.StationSearchablesSource.StationSearchable(dummyStation(1, "WW Gdanska", 1.2, 3.4), "def"),
        DistanceContract.StationSearchablesSource.StationSearchable(dummyStation(2, "WW Centralna", 5.6, 7.8), "ghi"),
        DistanceContract.StationSearchablesSource.StationSearchable(dummyStation(3, "WW Praga", 9.10, 10.11), "abc")
    )
    private val source: DistanceContract.StationSearchablesSource = mock {
        on { stationSearchables } doReturn Observable.just(searchables)
        on { stationSearchablesValue } doReturn searchables
    }
    private val calculateDistanceUseCase: UseCase<CalculateDistanceRequest, CalculateDistanceResponse> = mock {
        on { invoke(any()) } doReturn Single.just(CalculateDistanceResponse(Distance(1200.0, DistanceUnit.Meter)))
    }
    private val viewModel = CalculateStationsDistanceViewModel(source, calculateDistanceUseCase)

    @Test
    fun `test initial distance is null`() {
        viewModel.distance.test().assertValue(DistanceViewModel(null))
    }

    @Test
    fun `test viewModel returns null distance when startStation is not selected`() {
        val distanceTester = viewModel.distance.test()
        viewModel.startStationSelected(null)
        viewModel.endStationSelected(Station.Id(1))
        distanceTester.assertValue(DistanceViewModel(null))
    }

    @Test
    fun `test viewModel returns null distance when endStation is not selected`() {
        val distanceTester = viewModel.distance.test()
        viewModel.startStationSelected(Station.Id(1))
        viewModel.endStationSelected(null)
        distanceTester.assertValue(DistanceViewModel(null))
    }

    @Test
    fun `test viewModel returns distance when both stations are selected`() {
        val distanceTester = viewModel.distance.test()
        viewModel.startStationSelected(Station.Id(1))
        viewModel.endStationSelected(Station.Id(2))
        distanceTester.assertValue(DistanceViewModel(1.2f))
    }

    @Test
    fun `test viewModel uses calculateDistanceUseCase to calculate distance`() {
        viewModel.distance.test()
        viewModel.startStationSelected(Station.Id(1))
        viewModel.endStationSelected(Station.Id(2))
        verify(calculateDistanceUseCase).invoke(CalculateDistanceRequest(Location(1.2, 3.4), Location(5.6, 7.8)))
    }

    private fun dummyStation(id: Int, name: String, lat: Double, lng: Double) = Station(
        Station.Id(id),
        Station.Name(name),
        Location(lat, lng),
        0
    )
}