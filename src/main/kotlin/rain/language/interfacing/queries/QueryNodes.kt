package rain.language.interfacing.queries

import rain.graph.interfaceable.*
import rain.language.interfacing.NodeLabel

//abstract class QueryNodes: NodeQueryable {
//
//    operator fun <T:GraphableNode>invoke(label:NodeLabel<out T>): T Sequence<T>
//
//    // returns a sequence that represents the query results
//    abstract override operator fun invoke(): Sequence<GraphableNode>
//
//}
//
//class QueryNodesSelect(
//    val selectKeys: Array<out String> = arrayOf(),
//    val selectLabelName: String? = null
//): QueryNodes() {
//    override val method: QueryMethod = QueryMethod.SELECT
//    override operator fun invoke(): Sequence<GraphableNode> =
//}
//
//interface QueryNodesConnected: Query {
//    var connectFromQuery: Query
//    val relationshipLabelName: String
//    val relationshipRight: Boolean
//    val predicate: Filter? // optional additional filter after the connection
//}
//
//
//interface FilterQuery: Query {
//    val filterFromQuery: Query
//    val predicate: Filter
//
//}