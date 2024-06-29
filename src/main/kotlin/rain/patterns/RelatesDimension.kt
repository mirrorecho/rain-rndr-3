package rain.patterns

import rain.language.RelationshipLabel
import rain.language.Node
import rain.patterns.Pattern
import rain.patterns.relationships.CUES_FIRST

// base dimension for simple relationship-based items
open class RelatesDimension(
    pattern: Pattern,
    val relationshipLabel: RelationshipLabel,
    // an optional extended list of relationships, beyond the primary relationship, for querying (but not extending):
    vararg val extendedRelationships: RelationshipLabel
    ): Dimension(pattern, DimensionLabel.valueOf(relationshipLabel.labelName)) {

    override val graphableNodes =
        pattern.node.get(
            relationshipLabel(),
            *( extendedRelationships.map { it() }.toTypedArray() )
        ).graphableNodes

    override fun extend(vararg nodes: Node) {
        nodes.forEach {n-> pattern.node.relate(relationshipLabel, n) }
    }

}