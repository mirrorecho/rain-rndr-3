package rain.patterns.nodes

import rain.language.RelationshipLabel
import rain.language.interfaces.LanguageNode
import rain.patterns.interfaces.Dimension
import rain.patterns.interfaces.DimensionLabel
import rain.patterns.interfaces.Pattern

// base dimension for simple relationship-based items
open class RelatesDimension(
    final override val pattern: Pattern,
    val relationshipLabel: RelationshipLabel,
    // an optional extended list of relationships, beyond the primary relationship, for querying (but not extending):
    vararg val extendedRelationships: RelationshipLabel
    ): Dimension {

    override val label: DimensionLabel = DimensionLabel.valueOf(relationshipLabel.labelName)

    override fun copy(anotherPattern: Pattern): Dimension = RelatesDimension(anotherPattern, relationshipLabel, *extendedRelationships)

    fun getQuery(node: LanguageNode = pattern.node) = node.get( relationshipLabel(), *( extendedRelationships.map { it() }.toTypedArray()) )

    override fun invoke() = sequence { getQuery().forEach { n->
            yield(Pattern(n, this@RelatesDimension.pattern, this@RelatesDimension.label))
        } }

    override fun extend(vararg nodes: LanguageNode) {
        nodes.forEach {n-> pattern.node.relate(relationshipLabel, n) }
    }

}