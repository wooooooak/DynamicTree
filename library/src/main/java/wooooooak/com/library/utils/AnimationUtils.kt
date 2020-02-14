package wooooooak.com.orgviewsample.utils

import android.animation.TimeInterpolator
import android.animation.ValueAnimator

inline fun getValueAnimator(
    forward: Boolean = true,
    duration: Long,
    interpolator: TimeInterpolator,
    crossinline updateListener: (progress: Float) -> Unit
): ValueAnimator {
    val valueAnimator =
        if (forward) ValueAnimator.ofFloat(0f, 1f)
        else ValueAnimator.ofFloat(1f, 0f)
    valueAnimator.addUpdateListener { updateListener(it.animatedValue as Float) }
    valueAnimator.duration = duration
    valueAnimator.interpolator = interpolator
    return valueAnimator
}