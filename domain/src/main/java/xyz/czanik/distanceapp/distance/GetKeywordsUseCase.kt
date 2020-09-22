package xyz.czanik.distanceapp.distance

import io.reactivex.rxjava3.core.Single
import xyz.czanik.distanceapp.Repository
import xyz.czanik.distanceapp.UseCase
import xyz.czanik.distanceapp.entities.Keyword

object GetKeywordsRequest

data class GetKeywordsResponse(val keywords: List<Keyword>)

class GetKeywordsUseCase(
    private val keywordsRepository: Repository<List<Keyword>>
) : UseCase<GetKeywordsRequest, GetKeywordsResponse> {

    override fun invoke(request: GetKeywordsRequest): Single<GetKeywordsResponse> = keywordsRepository.get().map(::GetKeywordsResponse)
}