package rain.patterns.nodes

import rain.language.RelationshipLabel
import rain.patterns.interfaces.Dimension
import rain.patterns.interfaces.Pattern
import rain.patterns.interfaces.forEach

// similar to relates dimension, but searches up the history dimension for relationships
class RelatesHistoryDimension(
    pattern: Pattern,
    relationshipLabel: RelationshipLabel,
    vararg extendedRelationships: RelationshipLabel
): RelatesDimension(pattern, relationshipLabel, *extendedRelationships) {

    override fun copy(anotherPattern: Pattern): Dimension = RelatesHistoryDimension(anotherPattern, relationshipLabel, *extendedRelationships)

    override fun invoke() = sequence {
        pattern.history.forEach { h ->
            getQuery(h.node).forEach { n ->
                yield(Pattern(n, this@RelatesHistoryDimension.pattern, this@RelatesHistoryDimension.label))
            }
        }
    }
}