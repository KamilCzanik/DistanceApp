package xyz.czanik.distanceapp.distance

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.rxjava3.core.Single
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import xyz.czanik.distanceapp.UseCase
import xyz.czanik.distanceapp.distance.DistanceContract.CalculateDistanceViewModel.DistanceViewModel
import xyz.czanik.distanceapp.distance.DistanceContract.SearchViewModel.Query
import xyz.czanik.distanceapp.distance.DistanceContract.SearchViewModel.Result
import xyz.czanik.distanceapp.distance.DistanceContract.StationViewModel
import xyz.czanik.distanceapp.entities.Distance
import xyz.czanik.distanceapp.entities.DistanceUnit
import xyz.czanik.distanceapp.entities.Keyword
import xyz.czanik.distanceapp.entities.Location
import xyz.czanik.distanceapp.entities.Station

class StationsDistanceViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()
    private val stations = listOf(
        dummyStation(1, "WW Gdanska", 1.2, 3.4),
        dummyStation(2, "WW Centralna", 5.6, 7.8),
        dummyStation(3, "WW Praga", 9.10, 10.11)
    )
    private val keywords = listOf(
        dummyKeyword(3, "abc"),
        dummyKeyword(1, "def"),
        dummyKeyword(2, "ghi")
    )
    private val getStationsUseCase: UseCase<GetStationsRequest, GetStationsResponse> = mock {
        on { invoke(any()) } doReturn Single.just(GetStationsResponse(stations))
    }
    private val getKeywordsUseCase: UseCase<GetKeywordsRequest, GetKeywordsResponse> = mock {
        on { invoke(any()) } doReturn Single.just(GetKeywordsResponse(keywords))
    }
    private val calculateDistanceUseCase: UseCase<CalculateDistanceRequest, CalculateDistanceResponse> = mock {
        on { invoke(any()) } doReturn Single.just(CalculateDistanceResponse(Distance(1200.0, DistanceUnit.Meter)))
    }
    private val viewModel = StationsDistanceViewModel(getStationsUseCase, getKeywordsUseCase, calculateDistanceUseCase)

    @Test
    fun `test viewModel interacts with useCases`() {
        verify(getStationsUseCase).invoke(GetStationsRequest)
        verify(getKeywordsUseCase).invoke(GetKeywordsRequest)
    }

    @Test
    fun `test viewModel returns search results based on keywords`() = assertResultForQuery(
        listOf(StationViewModel(3, "WW Praga")),
        "ab"
    )

    @Test
    fun `test viewModel returns all stations as result on empty query`() = assertResultForQuery(
        stations.map { StationViewModel(it.id.value, it.name.value) },
        ""
    )

    @Test
    fun `test viewModel returns list with one result on direct query`() = assertResultForQuery(
        listOf(StationViewModel(2, "WW Centralna")),
        "ghi"
    )

    @Test
    fun `test viewModel return empty result when query is not matching any keyword`() = assertResultForQuery(
        emptyList(),
        "jkl"
    )

    @Test
    fun `test initial distance is null`() {
        viewModel.distance.test().assertValue(DistanceViewModel(null))
    }

    @Test
    fun `test viewModel returns null distance when startStation is not selected`() {
        viewModel.startStationSelected(null)
        viewModel.endStationSelected(Station.Id(1))
        viewModel.distance.test().assertValue(DistanceViewModel(null))
    }

    @Test
    fun `test viewModel returns null distance when endStation is not selected`() {
        viewModel.startStationSelected(Station.Id(1))
        viewModel.endStationSelected(null)
        viewModel.distance.test().assertValue(DistanceViewModel(null))
    }

    @Test
    fun `test viewModel returns distance when both stations are selected`() {
        viewModel.startStationSelected(Station.Id(1))
        viewModel.endStationSelected(Station.Id(2))
        viewModel.distance.test().assertValue(DistanceViewModel(1.2f))
    }

    @Test
    fun `test viewModel uses calculateDistanceUseCase to calculate distance`() {
        viewModel.startStationSelected(Station.Id(1))
        viewModel.endStationSelected(Station.Id(2))
        viewModel.distance.test()
        verify(calculateDistanceUseCase).invoke(CalculateDistanceRequest(Location(1.2, 3.4), Location(5.6, 7.8)))
    }

    private fun assertResultForQuery(resultValue: List<StationViewModel>, queryValue: String) = assertEquals(
        Result(resultValue),
        viewModel.search(Query(queryValue))
    )

    private fun dummyStation(id: Int, name: String, lat: Double, lng: Double) = Station(
        Station.Id(id),
        Station.Name(name),
        Location(lat, lng),
        0
    )

    private fun dummyKeyword(stationId: Int, value: String) = Keyword(Station.Id(stationId), value)
}