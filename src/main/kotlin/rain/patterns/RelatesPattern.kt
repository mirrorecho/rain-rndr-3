package rain.patterns

import rain.language.RelationshipLabel
import rain.language.Node

// base dimension for simple relationship-based items
open class RelatesPattern<ST:Node, DT:Node>(
    pattern: Pattern<ST, DT>,
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