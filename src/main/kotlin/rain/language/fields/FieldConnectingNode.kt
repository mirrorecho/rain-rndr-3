package rain.language.fields

import org.jetbrains.annotations.Nullable
import rain.language.Node
import rain.language.NodeLabel
import rain.language.RelationshipLabel
import rain.patterns.Pattern
import rain.patterns.RelatesPattern


open class FieldConnectingNode<T:Node?>(
    name: String,
    val patternFactory: (source: Node)-> Pattern<*>,
    val label: NodeLabel<T & Any>,
    default: T,
    cascade: Boolean = true
): Field<T>(name, default, cascade) {

    override val isNode: Boolean = true

    open inner class Attached(
        node: Node,
        val pattern: Pattern<*>,
    ): Field<T>.Attached(node) {

        override val isLocal: Boolean = false


        override fun store() {
            pattern.clear()
            value?.let { pattern.extend(it) }
        }

        override fun retrieve() {
            value = pattern(this@FieldConnectingNode.label).firstOrNull()
        }

    }

    override fun attach(node: Node): Attached = Attached(node, patternFactory(node))
}

//// ======================================================================

fun <T: Node?> field(
    name: String,
    relationshipLabel: RelationshipLabel,
    label: NodeLabel<T & Any>,
    default: T? = null,
    cascade: Boolean = true,
) =
    FieldConnectingNode(
        name,
        {s -> RelatesPattern(s, null, relationshipLabel) },
        label,
        default,
        cascade,
    )