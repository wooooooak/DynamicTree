package wooooooak.com.library.data


interface UniqueKey {
    val uniqueKey: String
    val child: List<UniqueKey>?
}
