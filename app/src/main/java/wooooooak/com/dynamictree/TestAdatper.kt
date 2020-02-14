package wooooooak.com.dynamictree

import android.view.View
import wooooooak.com.dynamictree.data.Node
import wooooooak.com.library.adapter.DynamicTreeAdapter

class TestAdapter: DynamicTreeAdapter<Node>(
    R.layout.item_group,
    R.layout.item_last_node
) {
    override fun isTargetNode(node: Node): Boolean {
        TODO("Used to check whether it is the last node or not")
    }

    override fun bindDefultLastNodeView(view: View, node: Node) {
        TODO("Bind default last node item view ")
    }

    override fun bindDefaultGroupView(view: View, node: Node) {
        TODO("Bind default group item view ")
    }

    override fun bindClickedGroupView(view: View, node: Node) {
        TODO("Bind group item view when clicked")
    }

    override fun onClickGroupItem(groupItemView: View, node: Node) {
        TODO("Called when group item clicked")
    }

    override fun onClickLastNodeItem(lastNodeItemView: View, node: Node) {
        TODO("Called when last node item clicked")
    }
}