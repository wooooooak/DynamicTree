package wooooooak.com.dynamictree

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import wooooooak.com.dynamictree.data.Node
import wooooooak.com.library.adapter.DynamicTreeAdapter

class MyDynamicTreeAdapter(
    private val onClickGroupItemListener: (node: Node) -> Unit,
    private val onClickLastItemListener: (node: Node) -> Unit
) : DynamicTreeAdapter<Node>(
    R.layout.item_group,
    R.layout.item_last_node
) {

    override fun isTargetNode(node: Node) = node.personInfo != null

    override fun bindDefaultGroupView(view: View, node: Node) {
        val itemView = view as ConstraintLayout
        itemView.run {
            val textView: TextView = findViewById(R.id.group_name)
            textView.text = node.uniqueKey
            val countTextView: TextView = findViewById(R.id.child_count)
            countTextView.text = "(${node.child?.count() ?: 0})"
            textView.setTextColor(ContextCompat.getColor(view.context, R.color.orgBlack))
        }
    }

    override fun bindDefultLastNodeView(view: View, node: Node) {
        val itemView = view as ConstraintLayout
        itemView.run {
            val nameTextView: TextView = findViewById(R.id.node_name)
            val ageTextView: TextView = findViewById(R.id.node_age)
            val numberTextView: TextView = findViewById(R.id.node_number)
            nameTextView.text = node.uniqueKey
            ageTextView.text = node.personInfo?.age.toString()
            numberTextView.text = node.personInfo?.number
        }
    }

    override fun bindClickedGroupView(view: View, node: Node) {
        view.findViewById<TextView>(R.id.group_name).run {
            setTextColor(ContextCompat.getColor(view.context, R.color.colorPrimary))
            text = node.uniqueKey
        }
    }

    override fun onClickGroupItem(groupItemView: View, node: Node) {
    }

    override fun onClickLastNodeItem(lastNodeItemView: View, node: Node) {
        onClickLastItemListener(node)
    }
}
