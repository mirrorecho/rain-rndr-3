package rain.graph

import rain.graph.interfaceable.*

typealias LabelsToRelationships =  MutableMap<String, MutableSet<GraphRelationship>>

class GraphNode(
    override val key:String,
    override val labels: List<String>,
    properties: Map<String, Any?> = mapOf()
) : GraphableNode {

    override fun toString():String = "GRAPH NODE: $labelName($key) $properties"

    override val properties: MutableMap<String, Any?> = properties.toMutableMap()

//    internal var connectedRelationships = mutableMapOf<LabelDirected, Pair<GraphRelationship, GraphNode>>()
    private val connectedRelationships = Pair<LabelsToRelationships, LabelsToRelationships>(
        mutableMapOf(), mutableMapOf(),
    )

    fun getLabelsToRelationships(directionRight: Boolean): LabelsToRelationships =
        if (directionRight) connectedRelationships.first else connectedRelationships.second

    fun connectRelationship(relationship: GraphRelationship, directionRight: Boolean) {
        getLabelsToRelationships(directionRight).getOrPut(relationship.labelName) { mutableSetOf() }.add(relationship)
    }

    fun disconnectRelationship(relationship: GraphRelationship, directionRight: Boolean) {
        getLabelsToRelationships(directionRight)[relationship.labelName]?.remove(relationship)
    }

    fun getRelationships(relationshipLabel: String, directionRight: Boolean): Set<GraphRelationship> =
        getLabelsToRelationships(directionRight)[relationshipLabel].orEmpty()

    //maps for faster indexing ... keys are relationships, values are the target nodes
//    internal val sourcesFor = mutableMapOf<GraphRelationship, GraphNode>()
//    internal val targetsFor = mutableMapOf<GraphRelationship, GraphNode>()

    override val labelName = labels[0]

    fun cleanup(graph: Graph) {
        labels.forEach { graph.discardLabelIndex(it, this) }

        arrayOf(true, false).forEach {dr->
            getLabelsToRelationships(dr).forEach {
                it.value.forEach { r-> graph.deleteRelationship(r.key) }
            }
        }

//        sourcesFor.asIterable().forEach { graph.deleteRelationship(it.key.key) }
//        targetsFor.asIterable().forEach { graph.deleteRelationship(it.key.key) }
    }
}