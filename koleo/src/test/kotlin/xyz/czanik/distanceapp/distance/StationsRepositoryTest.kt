package xyz.czanik.distanceapp.distance

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import io.reactivex.rxjava3.subjects.SingleSubject
import org.junit.Test
import xyz.czanik.distanceapp.entities.Location
import xyz.czanik.distanceapp.entities.Station

class StationsRepositoryTest {

    private val koleoStationsSubject = SingleSubject.create<List<KoleoStation>>()
    private val service: KoleoService = mock {
        on { allStations() } doReturn koleoStationsSubject
    }
    private val repository = StationsRepository(service)

    @Test
    fun `test repository does NOT interact with service`() {
        verifyZeroInteractions(service)
    }

    @Test
    fun `test repository interacts with service after get call`() {
        repository.get().test()
        verify(service).allStations()
    }

    @Test
    fun `test repository maps response to domain Stations`() {
        val repositoryTester = repository.get().test()
        koleoStationsSubject.onSuccess(stubResponse())
        val expected = listOf(
            Station(Station.Id(1), Station.Name("Warszawa Gdanska"), Location(2.0, 3.0), 1),
            Station(Station.Id(2), Station.Name("Warszawa Centralna"), Location(3.0, 4.0), 2)
        )
        repositoryTester.assertValue(expected)
    }

    @Test
    fun `test repository forwards error from service`() {
        val repositoryTester = repository.get().test()
        val error = Throwable(":(")
        koleoStationsSubject.onError(error)
        repositoryTester.assertError(error)
    }

    private fun stubResponse(): List<KoleoStation> = listOf(
        KoleoStation(1, "Warszawa Gdanska", 2.0, 3.0, 1),
        KoleoStation(2, "Warszawa Centralna", 3.0, 4.0, 2)
    )
}