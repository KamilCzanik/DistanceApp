package xyz.czanik.distanceapp

import android.app.Application

class DistanceApp : Application(), Container {

    override lateinit var repositoriesFactory: RepositoriesFactory

    override fun onCreate() {
        repositoriesFactory = KoleoRepositoriesFactory()
        super.onCreate()
    }
}

