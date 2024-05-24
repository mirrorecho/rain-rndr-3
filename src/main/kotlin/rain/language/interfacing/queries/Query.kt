package rain.language.interfacing.queries

import rain.graph.interfaceable.*
import rain.language.Context
import rain.language.Item
import rain.language.LocalContext
import rain.language.Node
import rain.language.interfacing.Label
import rain.language.interfacing.NodeLabel

typealias Filter = (GraphableItem)->Boolean

//enum class QueryDomain() {
//    RELATIONSHIPS_RIGHT,
//    RELATIONSHIPS_LEFT,
//    NODES,
//}

enum class QueryMethod {
    SELECT,
    FROM,
    CONNECTED, // queries relationships connected to nodes and vice versa
}


open class Query<T:GraphableItem>(
    val queryFun: (Query<*>) -> Sequence<T>,
    val method: QueryMethod = QueryMethod.SELECT,
//    val labelDirected: LabelDirected,
    val labelName: String? = null,
    val directionRight: Boolean? = null, // used for relationship selects
    val selectKeys: Array<out String> = arrayOf(),
    val predicate: Filter? = null,

    val queryFrom: Sequence<Query<T>>? = null,
    val queryConnected: Query<*>? = null,
    override val context: Context = LocalContext,

    ): Queryable {
    override val queryMe get() = this

    fun getItems(): Sequence<T> = queryFun(this)

//    operator fun <IT: Item>invoke(label: Label<IT>?): Sequence<T> = getItems().map { label.from(it) }

    operator fun invoke(): Sequence<T> = getItems().map { label.from(it) }

    inline fun forEach(block: (Node)->Unit) = invoke().forEach(block)

    override fun selectKeys(): Sequence<String> = context.selectNodeKeys(this)

}

//open class QueryConnected<T:GraphableItem, CT: GraphableItem>(
//    queryFun: (Query<T>) -> Sequence<T>,
//    predicate: Filter? = null,
//    var queryFromConnected: Query<CT>? = null,
//): Query<T>(queryFun, QueryMethod.CONNECTED, predicate=predicate) {
//
//        var rootQuery: Query<GraphableItem>
//        get() = queryFromConnected?.rootQuery ?: this
//        set(query) { rootQuery.queryFromConnected = query }
//
//}



fun selectNodes(label: NodeLabel<*>, vararg keys: String) = Query(queryFun = LocalContext.graph::queryNodes )

fun selectNodes(vararg keys: String) = Query(queryFun = LocalContext.graph::queryNodes, selectKeys = keys)

//
//interface Query<T:GraphableItem>: Queryable {
//
//    abstract val method: QueryMethod
////    abstract val domain: QueryDomain
//
//    abstract fun getItems(): Sequence<T>
//
//}
//
//
//abstract class SelectQuery2<T:GraphableItem>: Query<T> {
//    abstract val selectKeys: Array<String>
//    abstract val selectLabelName:String?
//    override val method = QueryMethod.SELECT
//}
//
//abstract class ConnectedQuery2<T:GraphableItem, FT:GraphableItem>: Query<T> {
//    abstract val connectFrom:Query<FT>
//}
//
//abstract class FilterQuery2<T:GraphableItem>: Query<T> {
//    abstract val predicate: (T)->Boolean
//    abstract val queryFrom:Query<T>
//}
//
//abstract class ConcatQuery2<T:GraphableItem>: Query<T> {
//    abstract val queries: Array<T>
//}
//
//
//class QuerySelectNodes(
//    override val selectKeys: Array<String> = arrayOf(),
//    override val selectLabelName:String? = null
//): SelectQuery2<GraphableNode>() {
//    override fun getItems(): Sequence<GraphableNode> = sequence {  }
//}
//
//abstract class QueryNodes2: Query<Node>() {
//
////    abstract val method: QueryMethod
////    abstract val domain: QueryDomain
//
//    override operator fun <T:Node>invoke(label: Label<T>?): Sequence<T> = super.invoke(label).map { label.from(it) }
//
//}
//
//
//
//
//abstract class  RelationshipsQuery: Query() {
//
//    val method: QueryMethod
//    val domain: QueryDomain
//
//    // returns a sequence that represents the query results
//    abstract override operator fun invoke(): Sequence<GraphableItem>
//
//}
//
//
//
//interface ConcatQuery: Query {
//    val queries: Array<Query>
//}


//interface Query: Queryable {
//
//    val selectKeys: Array<out String>
//
//    val predicate: Filter?
//
//    val method: QueryMethod
//    val domain: QueryDomain
//
//    // returns a sequence that represents the query results
//    operator fun invoke(): Sequence<GraphableItem>
//
//    fun asKeys(): Sequence<String>
//
//    var fromQuery: Query?
//
//    var rootQuery: Query
//        get() = fromQuery?.rootQuery ?: this
//        set(query) { rootQuery.fromQuery = query }
//}

