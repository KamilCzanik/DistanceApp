package xyz.czanik.distanceapp.util

import java.math.RoundingMode

fun Double.roundOffDecimal(): Double = toBigDecimal()
        .setScale(2, RoundingMode.UP)
        .toDouble()