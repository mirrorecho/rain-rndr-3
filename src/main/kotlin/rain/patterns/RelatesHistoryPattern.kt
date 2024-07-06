package rain.patterns

import rain.graph.interfacing.GraphableNode
import rain.language.Node
import rain.language.NodeLabel
import rain.language.RelationshipLabel


open class RelatesHistoryPattern<T: Node, ST:T, DT:T>(
    source: ST,
    destinationLabel: NodeLabel<DT>,
    previous: Pattern<T, *, *>? = null,
    relationshipLabel: RelationshipLabel,
    // an optional extended list of relationships, beyond the primary relationship, for querying (but not extending):
    vararg extendedRelationships: RelationshipLabel):
    RelatesPattern<T, ST, DT>(source, destinationLabel, previous, relationshipLabel, *extendedRelationships) {

//    override fun copy(anotherPattern: Pattern): Dimension = RelatesHistoryDimension(anotherPattern, relationshipLabel, *extendedRelationships)

    override val graphableNodes = sequence<GraphableNode> {
        history.forEach { hn ->
            yieldAll(
                hn.get(
                    relationshipLabel(),
                    *(extendedRelationships.map { it() }.toTypedArray())
                ).graphableNodes
            )
        }
    }

}