package xyz.czanik.distanceapp.distance

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.rxjava3.core.Observable
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import xyz.czanik.distanceapp.distance.DistanceContract.SearchViewModel.Query
import xyz.czanik.distanceapp.distance.DistanceContract.StationSearchablesSource.StationSearchable
import xyz.czanik.distanceapp.distance.DistanceContract.StationViewModel
import xyz.czanik.distanceapp.entities.Location
import xyz.czanik.distanceapp.entities.Station

class SearchStationsViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()
    private val searchables = listOf(
        StationSearchable(dummyStation(1, "WW Gdanska", 1.2, 3.4), "def"),
        StationSearchable(dummyStation(2, "WW Centralna", 5.6, 7.8), "ghi"),
        StationSearchable(dummyStation(3, "WW Praga", 9.10, 10.11), "abc")
    )
    private val source: DistanceContract.StationSearchablesSource = mock {
        on { stationSearchables } doReturn Observable.just(searchables)
        on { stationSearchablesValue } doReturn searchables
    }
    private val viewModel = SearchStationsViewModel(source)

    @Test
    fun `test viewModel interacts with source`() {
        viewModel.search(Query("_"))
        verify(source).stationSearchablesValue
    }

    @Test
    fun `test viewModel returns search results based on keywords`() = assertResultForQuery(
        listOf(StationViewModel(3, "WW Praga")),
        "ab"
    )

    @Test
    fun `test viewModel returns all stations as result on empty query`() = assertResultForQuery(
        searchables.map { StationViewModel(it.station.id.value, it.station.name.value) },
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

    private fun assertResultForQuery(resultValue: List<StationViewModel>, queryValue: String) = Assert.assertEquals(
        DistanceContract.SearchViewModel.Result(resultValue),
        viewModel.search(Query(queryValue))
    )

    private fun dummyStation(id: Int, name: String, lat: Double, lng: Double) = Station(
        Station.Id(id),
        Station.Name(name),
        Location(lat, lng),
        0
    )
}