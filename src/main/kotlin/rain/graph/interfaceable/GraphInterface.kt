package rain.graph.interfaceable

import rain.graph.Graph
import rain.graph.GraphNode
import rain.graph.GraphRelationship
import rain.language._bak2.SelectInterface
import rain.language.interfacing.queries.Query

typealias LabelDirected = Pair<String, Boolean?>


interface GraphInterface {

    // NOTE: this was a dunder method in python implementation
    // TODO: needed?
    fun contains(key: String): Boolean

    // NOTE: this was named exists in python implementation
    fun contains(node:GraphableNode): Boolean

    fun contains(relationship:GraphableRelationship): Boolean

    fun create(node:GraphableNode)

    fun create(relationship:GraphableRelationship)

    fun merge(node:GraphableNode)

    fun merge(relationship:GraphableRelationship)

    fun read(node:GraphableNode)

    fun read(relationship:GraphableRelationship)

    // TODO: assume not needed
//    fun readRelationship(item:GraphableItem)

    fun getNode(key:String): GraphableNode

    fun getRelationship(key:String): GraphableRelationship

    fun save(node:GraphableNode)

    fun save(relationship:GraphableRelationship)

    fun deleteNode(key: String)

    fun deleteRelationship(key: String)

    fun queryNodes(query: Query<*>): Sequence<GraphNode>

    fun queryRelationships(query: Query<*>): Sequence<GraphRelationship>

//    fun <T: LanguageItem>selectItems(select: SelectInterface, factory:):Sequence<T>

}

fun localGraph(): GraphInterface = Graph()

