package rain.patterns

import rain.graph.interfacing.GraphableNode
import rain.language.RelationshipLabel


// similar to relates dimension, but searches up the history dimension for relationships
class RelatesHistoryDimension(
    pattern: Pattern,
    relationshipLabel: RelationshipLabel,
    vararg extendedRelationships: RelationshipLabel
): RelatesDimension(pattern, relationshipLabel, *extendedRelationships) {

//    override fun copy(anotherPattern: Pattern): Dimension = RelatesHistoryDimension(anotherPattern, relationshipLabel, *extendedRelationships)

    override val graphableNodes = sequence<GraphableNode> {
        pattern.history.forEach { hn ->
            hn.get(
                relationshipLabel(),
                *(extendedRelationships.map { it() }.toTypedArray())
            ).graphableNodes
        }
    }

}