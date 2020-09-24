package xyz.czanik.distanceapp.distance

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
import xyz.czanik.distanceapp.distance.DistanceContract.Query
import xyz.czanik.distanceapp.distance.DistanceContract.Result
import xyz.czanik.distanceapp.distance.DistanceContract.StationViewModel
import xyz.czanik.distanceapp.entities.Keyword
import xyz.czanik.distanceapp.entities.Location
import xyz.czanik.distanceapp.entities.Station

class StationsDistanceViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()
    private val stations = listOf(
        dummyStation(1, "WW Gdanska"),
        dummyStation(2, "WW Centralna"),
        dummyStation(3, "WW Praga")
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
    private val viewModel = StationsDistanceViewModel(getStationsUseCase, getKeywordsUseCase)

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

    private fun assertResultForQuery(resultValue: List<StationViewModel>, queryValue: String) = assertEquals(
        Result(resultValue),
        viewModel.search(Query(queryValue))
    )

    private fun dummyStation(id: Int, name: String) = Station(Station.Id(id), Station.Name(name), Location(0.0, 0.0), 0)

    private fun dummyKeyword(stationId: Int, value: String) = Keyword(Station.Id(stationId), value)
}