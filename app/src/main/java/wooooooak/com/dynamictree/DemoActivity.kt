package wooooooak.com.dynamictree

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_demo.*
import wooooooak.com.dynamictree.data.TreeList
import wooooooak.com.library.view.DynamicTreeView

class DemoActivity : AppCompatActivity() {

    private val item: TreeList by lazy {
        val inputStream = assets.open("sample.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val data = Gson().fromJson(jsonString, TreeList::class.java)
        data as TreeList
    }

    private val dynamicView: DynamicTreeView by lazy {
        org_view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)

        dynamicView.treeData = item.data
        dynamicView.orgAdapterGenerator = {
            MyDynamicTreeAdapter(
                onClickGroupItemListener = {
                },
                onClickLastItemListener = {
                    Toast.makeText(baseContext, it.uniqueKey, Toast.LENGTH_SHORT).show()
                }
            )
        }
        dynamicView.render()
    }

    override fun onBackPressed() {
        if (dynamicView.canGoBack()) {
            dynamicView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}