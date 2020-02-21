Android view library that represents **tree structure data**.

## Sample

![tree_sample](https://user-images.githubusercontent.com/18481078/74503092-2de30300-4f33-11ea-9ca1-a5a60b820cf4.gif)

## Install

### Gradle

1. Add the JitPack repository to your project level build.gradle file

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

2. Add the dependency to your app level build.gradle file

```
dependencies {
    implementation "com.github.wooooooak.DynamicTree:$version"
    implementation "androidx.recyclerview:recyclerview:$recyclerViewVersion"
}
```

The latest version is 1.0.0. Checkout [here](https://github.com/wooooooak/DynamicTree/releases)

## Usage

### 1. Set resource files (layout and color)

#### layout.xml

```xml
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    ...
    >
    <wooooooak.com.library.view.DynamicTreeView
        android:id="@+id/dt_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:dt_animation_duration="250"
        app:dt_depth_limit="3"
        app:dt_list_page_margin="8dp"
        app:dt_stack_view_color_array="@array/dtColorArray" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

- **dt_list_page_margin** : determines how overlapping the view looks. If the page goes beyond the `dt_depth_limit`, the new data is rendered but the depth shown to the user is still the depth you set.

- **dt_stack_view_color_array** : "dt_stack_view_color_array" should be one more than "dt_depth_limit".

<img width="987" alt="view_attrs" src="https://user-images.githubusercontent.com/18481078/74507897-13178b00-4f41-11ea-8550-8320a6da3d49.png">

#### colors.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    ...
    <array name="dtColorArray">
        <item>#74c0fc</item>
        <item>#a5d8ff</item>
        <item>#d0ebff</item>
        <item>#e7f5ff</item>
    </array>
</resources>
```

### 2. Prepare Tree Data

We need a list of tree. **And Each node must implement UniqueKey for search algorithm.**

```kotlin
// For Eaxample

data class Node(
    override val uniqueKey: String,  // you should override
    override val child: List<Node>?,  // you should override
    val personInfo: Person? // or anything more
) : UniqueKey

data class Person(
    val age: Int,
    val number: String
)
```

Finally, DynamicTree uses the node list.

```kotlin
 dt_view.treeData = listOf<Node>(...)
```

### 3. Make OrgGroupAdapter

```kotlin
class TestAdapter: DynamicTreeAdapter<Node>(
    R.layout.item_group, // layout id for Group item view
    R.layout.item_last_node // layout id for Last node item view
) {
    override fun isTargetNode(item: Node): Boolean {
        TODO("Used to check whether it is the last node or not")
    }

    override fun bindDefultLastNodeView(view: View, item: Node) {
        TODO("Bind default last node view ")
    }

    override fun bindDefaultGroupView(view: View, item: Node) {
        TODO("Bind default group item view ")
    }

    override fun bindClickedGroupView(view: View, item: Node) {
        TODO("Bind group item view when clicked")
    }

    override fun onClickGroupItem(groupItemView: View, item: Node) {
        TODO("Called when group item clicked")
    }

    override fun onClickLastNodeItem(lastNodeItemView: View, item: Node) {
        TODO("Called when last node clicked")
    }
}
```

Note that DynamicTreeAdapter takes the **UniquKey type** as generic.

### 4. In your Activity or Fragment

```kotlin
    override fun onCreate(savedInstanceState: Bundle?) {
        ...
        val treeList: List<Node> = ...
        dt_view.treeData = treeList
        dt_view.orgAdapterGenerator = { MyOrgGroupAdapter() }
        dt_view.render()
    }

    override fun onBackPressed() {
        if (dt_view.canGoBack()) {
            dt_view.goBack()
        } else {
            super.onBackPressed()
        }
    }
```

**You can clone this project and run the demo with sample data immediately.**

## License

```
Copyright 2020 wooooooak (Yongjun LEE)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
