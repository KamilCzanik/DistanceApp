package xyz.czanik.distanceapp.distance

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import io.reactivex.rxjava3.subjects.SingleSubject
import org.junit.Test
import xyz.czanik.distanceapp.Repository
import xyz.czanik.distanceapp.entities.Location
import xyz.czanik.distanceapp.entities.Station
import xyz.czanik.distanceapp.entities.Station.Id
import xyz.czanik.distanceapp.entities.Station.Name

internal class GetStationsUseCaseTest {

    private val stations = listOf(
        Station(Id(1), Name("Warszawa Gdanska"), Location(1.0, 1.0), 1),
        Station(Id(2), Name("Warszawa Centralna"), Location(2.0, 2.0), 2),
        Station(Id(3), Name("Warszawa Praga"), Location(3.0, 3.0), 3)
    )
    private val stationsSubject = SingleSubject.create<List<Station>>()
    private val repository: Repository<List<Station>> = mock {
        on { get() } doReturn stationsSubject
    }
    private val useCase = GetStationsUseCase(repository)

    @Test
    fun `test useCase does NOT interact with repository until invoke call`() {
        verifyZeroInteractions(repository)
    }

    @Test
    fun `test useCase interacts with repository after invoke call`() {
        useCase.invoke(GetStationsRequest).test()
        verify(repository).get()
    }

    @Test
    fun `test useCase returns response with stations provided by repository`() {
        val useCaseTester = useCase.invoke(GetStationsRequest).test()
        stationsSubject.onSuccess(stations)
        useCaseTester.assertValue(GetStationsResponse(stations))
    }

    @Test
    fun `test useCase forwards errors`() {
        val useCaseTester = useCase.invoke(GetStationsRequest).test()
        val error = Throwable(":(")
        stationsSubject.onError(error)
        useCaseTester.assertError(error)
    }
}