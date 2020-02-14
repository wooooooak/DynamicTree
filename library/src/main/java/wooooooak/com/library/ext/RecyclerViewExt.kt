package wooooooak.com.library.ext

import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView

internal fun RecyclerView.clearBackground() {
    children.forEach { it.background = null }
}