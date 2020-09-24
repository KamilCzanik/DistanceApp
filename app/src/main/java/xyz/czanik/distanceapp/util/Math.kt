package xyz.czanik.distanceapp.util

import java.math.RoundingMode
import java.text.DecimalFormat

fun Double.roundOffDecimal(): Double = DecimalFormat("#.##")
        .also { it.roundingMode = RoundingMode.UP }
        .format(this)
        .toDouble()