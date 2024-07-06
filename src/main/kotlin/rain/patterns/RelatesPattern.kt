package rain.patterns

import rain.language.RelationshipLabel
import rain.language.Node
import rain.language.NodeLabel

// base dimension for simple relationship-based items
open class RelatesPattern<T:Node, ST:T, DT:T>(
    source: ST,
    destinationLabel: NodeLabel<DT>,
    previous: Pattern<T, *, *>? = null,
    val relationshipLabel: RelationshipLabel,
    // an optional extended list of relationships, beyond the primary relationship, for querying (but not extending):
    vararg val extendedRelationships: RelationshipLabel
): Pattern<T, ST, DT>(source, destinationLabel, previous) {

    private val relatesQuery = this.source.get(
        relationshipLabel(),
        *(extendedRelationships.map { it() }.toTypedArray())
    )

    override val graphableNodes get() = relatesQuery.graphableNodes

    override fun extend(vararg nodes: Node) {
        nodes.forEach { n -> source.relate(relationshipLabel, n) }
    }

    // TODO: implement...
    override fun clear(deleteNodes:Boolean) = warningNotImplemented("clear")

}