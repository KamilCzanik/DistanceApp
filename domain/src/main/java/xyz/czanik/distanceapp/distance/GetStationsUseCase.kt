package xyz.czanik.distanceapp.distance

import io.reactivex.rxjava3.core.Single
import xyz.czanik.distanceapp.Repository
import xyz.czanik.distanceapp.UseCase
import xyz.czanik.distanceapp.entities.Station

object GetStationsRequest

data class GetStationsResponse(val stations: List<Station>)

class GetStationsUseCase(
    private val stationsRepository: Repository<List<Station>>
) : UseCase<GetStationsRequest, GetStationsResponse> {
    override fun invoke(request: GetStationsRequest): Single<GetStationsResponse> = stationsRepository.get().map(::GetStationsResponse)
}