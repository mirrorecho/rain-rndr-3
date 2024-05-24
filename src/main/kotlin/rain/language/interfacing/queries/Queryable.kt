package rain.language.interfacing.queries

import rain.graph.interfaceable.GraphableItem
import rain.language.Context
import rain.language.NodeQuery

// TODO: is this interface even worth it?
interface Queryable {
    val context: Context
    val queryMe: Query<*>

//    fun query(query: Query): Query

    // TODO: see if we can KISS, and leave these out!
//    val graph: GraphInterface get() = context.graph

}

interface NodeQueryable: Queryable {
    override val queryMe: NodeQuery

    // TODO: re-test!
    operator fun get(vararg queries: NodeQuery): NodeQuery {
        var returnQuery: NodeQuery = queryMe
        queries.forEach { it.rootQuery = returnQuery; returnQuery = it}
        return returnQuery
    }

}