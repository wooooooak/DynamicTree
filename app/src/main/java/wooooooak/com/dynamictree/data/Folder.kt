package wooooooak.com.dynamictree.data

import com.google.gson.annotations.SerializedName
import wooooooak.com.library.data.UniqueKey

data class TreeList(
    val data: List<Node>?
)

data class Node(
    @SerializedName("name")
    override val uniqueKey: String,
    override val child: List<Node>?,
    @SerializedName("info")
    val personInfo: Person?
) : UniqueKey

data class Person(
    val age: Int,
    val number: String
)
