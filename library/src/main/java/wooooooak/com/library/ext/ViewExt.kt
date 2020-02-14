package wooooooak.com.library.ext

import android.graphics.drawable.ColorDrawable
import android.view.View

internal fun View.makeBackgroundColorDark() {
    background?.let {
        setBackgroundColor((it as ColorDrawable).color.darken)
    }
}

internal fun View.makeBackgroundColorLight() {
    background?.let {
        setBackgroundColor((it as ColorDrawable).color.lighter)
    }
}