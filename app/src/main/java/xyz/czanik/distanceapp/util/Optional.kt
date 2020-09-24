package xyz.czanik.distanceapp.util

data class Optional<T>(val value: T?) {

    fun hasValue(): Boolean = value != null

    fun requireValue(): T = value!!

    companion object {

        fun <T> empty() = Optional<T>(null)

        fun <T> of(value: T) = Optional(value)

        fun <T> ofNullable(value: T?) = Optional(value)
    }
}