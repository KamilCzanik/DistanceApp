package xyz.czanik.distanceapp.distance

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import io.reactivex.rxjava3.subjects.SingleSubject
import org.junit.Test
import xyz.czanik.distanceapp.Repository
import xyz.czanik.distanceapp.entities.Keyword
import xyz.czanik.distanceapp.entities.Station

internal class GetKeywordsUseCaseTest {

    private val stations = listOf(
        Keyword(Station.Id(1), "gdanska"),
        Keyword(Station.Id(2), "centralna")
    )
    private val keywordsSubject = SingleSubject.create<List<Keyword>>()
    private val repository: Repository<List<Keyword>> = mock {
        on { get() } doReturn keywordsSubject
    }
    private val useCase = GetKeywordsUseCase(repository)

    @Test
    fun `test useCase does NOT interact with repository until invoke call`() {
        verifyZeroInteractions(repository)
    }

    @Test
    fun `test useCase interacts with repository after invoke call`() {
        useCase.invoke(GetKeywordsRequest).test()
        verify(repository).get()
    }

    @Test
    fun `test useCase returns response with stations provided by repository`() {
        val useCaseTester = useCase.invoke(GetKeywordsRequest).test()
        keywordsSubject.onSuccess(stations)
        useCaseTester.assertValue(GetKeywordsResponse(stations))
    }

    @Test
    fun `test useCase forwards errors`() {
        val useCaseTester = useCase.invoke(GetKeywordsRequest).test()
        val error = Throwable(":(")
        keywordsSubject.onError(error)
        useCaseTester.assertError(error)
    }
}