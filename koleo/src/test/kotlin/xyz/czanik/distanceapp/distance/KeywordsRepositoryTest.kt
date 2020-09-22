package xyz.czanik.distanceapp.distance

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import io.reactivex.rxjava3.subjects.SingleSubject
import org.junit.Test
import xyz.czanik.distanceapp.entities.Keyword
import xyz.czanik.distanceapp.entities.Station

class KeywordsRepositoryTest {

    private val koleoStationsSubject = SingleSubject.create<List<KoleoKeyword>>()
    private val service: KoleoService = mock {
        on { allKeywords() } doReturn koleoStationsSubject
    }
    private val repository = KeywordsRepository(service)

    @Test
    fun `test repository does NOT interact with service`() {
        verifyZeroInteractions(service)
    }

    @Test
    fun `test repository interacts with service after get call`() {
        repository.get().test()
        verify(service).allKeywords()
    }

    @Test
    fun `test repository maps response to domain Stations`() {
        val repositoryTester = repository.get().test()
        koleoStationsSubject.onSuccess(stubResponse())
        val expected = listOf(Keyword(Station.Id(1), "gdanska"), Keyword(Station.Id(2), "centralna"))
        repositoryTester.assertValue(expected)
    }

    @Test
    fun `test repository forwards error from service`() {
        val repositoryTester = repository.get().test()
        val error = Throwable(":(")
        koleoStationsSubject.onError(error)
        repositoryTester.assertError(error)
    }

    private fun stubResponse(): List<KoleoKeyword> = listOf(
        KoleoKeyword(1, "gdanska"),
        KoleoKeyword(2, "centralna")
    )
}