package xyz.czanik.distanceapp

import xyz.czanik.distanceapp.entities.Keyword
import xyz.czanik.distanceapp.entities.Station

interface RepositoriesFactory {

    fun stations(): Repository<List<Station>>

    fun keywords(): Repository<List<Keyword>>
}