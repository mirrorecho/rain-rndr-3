package rain.language

import rain.language.interfacing.queries.*
import rain.language.interfacing.*
import rain.patterns.interfaces.Pattern



abstract class NodeQuery: Query, NodeQueryable {

    override val selectKeys: Array<out String> = arrayOf()
    override val labelName: String? = null
    override val predicate: Filter? = null
    override var fromQuery: Query? = null
    override var context: Context = LocalContext // TODO maybe: explicitly pass context along when selecting?

    override val queryMe get() = this

    override val method = QueryMethod.ALL
    override val domain = QueryDomain.NODES

    override operator fun invoke(): Sequence<Node> = context.queryNodes(this)

    // TODO: breaking separation of concern with interfaces here... maybe, create a PatternInterface?
    fun asPatterns(): Sequence<Pattern> = this().map { it.makePattern() }

    inline fun forEach(block: (Node)->Unit) = this().forEach(block)

    override fun asKeys(): Sequence<String> = context.selectNodeKeys(this)

}

fun NodeQuery.filter(predicate: Filter): Query {
    return this[FilterNodes(predicate = predicate)]
}

class SelectLabelNodes(
    label: NodeLabel<*>
): NodeQuery() {

}

class FilterNodes(
    override var fromQuery: Query? = null,
    override var predicate: Filter
): NodeQuery() {
    override val method = QueryMethod.FILTER
}

class SelectNodes(
    label: NodeLabel<*>? = null,
    override vararg val selectKeys: String,
): NodeQuery() {
    override val labelName: String? = label?.labelName
    override val method = QueryMethod.SELECT
}