package wooooooak.com.library.ext

import android.graphics.Color
import androidx.annotation.ColorInt
import kotlin.math.max
import kotlin.math.min


internal inline val @receiver:ColorInt Int.darken
    @ColorInt
    get() = darken(this, 0.15)

internal inline val @receiver:ColorInt Int.lighter
    @ColorInt
    get() = lighten(this, 0.183)

private fun lighten(color: Int, fraction: Double): Int {
    var red = Color.red(color)
    var green = Color.green(color)
    var blue = Color.blue(color)
    red = lightenColor(red, fraction)
    green = lightenColor(green, fraction)
    blue = lightenColor(blue, fraction)
    val alpha = Color.alpha(color)
    return Color.argb(alpha, red, green, blue)
}

private fun darken(color: Int, fraction: Double): Int {
    var red = Color.red(color)
    var green = Color.green(color)
    var blue = Color.blue(color)
    red = darkenColor(red, fraction)
    green = darkenColor(green, fraction)
    blue = darkenColor(blue, fraction)
    val alpha = Color.alpha(color)
    return Color.argb(alpha, red, green, blue)
}

private fun darkenColor(color: Int, fraction: Double): Int {
    return max(color - color * fraction, 0.0).toInt()
}

private fun lightenColor(color: Int, fraction: Double): Int {
    return min(color + color * fraction, 255.0).toInt()
}