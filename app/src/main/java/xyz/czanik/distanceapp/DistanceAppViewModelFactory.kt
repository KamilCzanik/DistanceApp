package xyz.czanik.distanceapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import xyz.czanik.distanceapp.distance.GetKeywordsUseCase
import xyz.czanik.distanceapp.distance.GetStationsUseCase
import xyz.czanik.distanceapp.distance.StationsDistanceViewModel

class DistanceAppViewModelFactory(
    private val repositoriesFactory: RepositoriesFactory
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(StationsDistanceViewModel::class.java) -> modelClass.cast(
            StationsDistanceViewModel(
                GetStationsUseCase(repositoriesFactory.stations()),
                GetKeywordsUseCase(repositoriesFactory.keywords())
            )
        )!!
        else -> throw IllegalArgumentException("Unknown ViewModel class")
    }
}