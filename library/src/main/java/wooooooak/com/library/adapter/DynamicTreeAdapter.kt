package wooooooak.com.library.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import wooooooak.com.library.data.UniqueKey
import java.util.*

abstract class DynamicTreeAdapter<T : UniqueKey>(
    private val groupLayout: Int,
    private val lastNodeLayout: Int
) : ListAdapter<T, RecyclerView.ViewHolder>(DiffCallback()) {

    internal var currentDepth: Int = 0

    internal var onClickGroupListener: ((uniqueKey: String, depth: Int) -> Unit)? = null

    private var clickedKey = UUID.randomUUID().toString()

    private var clickedViewPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val isTargetNode = viewType == LAST_NODE_TYPE
        return if (isTargetNode) LastNodeViewHolder(
            LayoutInflater.from(parent.context).inflate(
                lastNodeLayout,
                parent, false
            )
        ) else GroupViewHolder(
            LayoutInflater.from(parent.context).inflate(
                groupLayout,
                parent, false
            )
        )
    }

    override fun getItemViewType(position: Int) = if (isTargetNode(getItem(position))) LAST_NODE_TYPE else GROUP_TYPE

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (isTargetNode(item)) {
            (holder as DynamicTreeAdapter<*>.LastNodeViewHolder).bind(position)
        } else {
            (holder as DynamicTreeAdapter<*>.GroupViewHolder).bind(position)
        }
    }

    protected abstract fun onClickGroupItem(groupItemView: View, item: T)

    protected abstract fun onClickLastNodeItem(lastNodeItemView: View, item: T)

    protected abstract fun isTargetNode(item: T): Boolean

    protected abstract fun bindDefultLastNodeView(view: View, item: T)

    protected abstract fun bindDefaultGroupView(view: View, item: T)

    protected abstract fun bindClickedGroupView(view: View, item: T)

    inner class GroupViewHolder(_view: View) : RecyclerView.ViewHolder(_view) {
        fun bind(position: Int) {
            val item = getItem(position)
            if (item.uniqueKey != clickedKey) {
                bindDefaultGroupView(itemView, item)
            } else {
                bindClickedGroupView(itemView, item)
            }
            itemView.setOnClickListener {
                onClickGroupItem(itemView, item)
                onClickGroupListener?.invoke(item.uniqueKey, currentDepth)
                bindClickedGroupView(itemView, item)
                notifyItemChanged(clickedViewPosition)
                clickedKey = item.uniqueKey
                clickedViewPosition = position
            }
        }
    }

    inner class LastNodeViewHolder(_view: View) : RecyclerView.ViewHolder(_view) {
        fun bind(position: Int) {
            val item = getItem(position)
            bindDefultLastNodeView(itemView, item)
            itemView.setOnClickListener {
                onClickLastNodeItem(itemView, item)
            }
        }
    }

    companion object {
        const val GROUP_TYPE = 0
        const val LAST_NODE_TYPE = 1
    }
}

private class DiffCallback<T : UniqueKey> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T) =
        oldItem.uniqueKey == newItem.uniqueKey

    override fun areContentsTheSame(oldItem: T, newItem: T) = false
}