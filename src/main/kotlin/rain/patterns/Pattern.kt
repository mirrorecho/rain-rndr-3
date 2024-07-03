package rain.patterns

import rain.graph.interfacing.GraphableNode
import rain.language.*
import rain.utils.autoKey

// patterns are abstractions of queries
open class Pattern<ST: Node, DT:Node>(
    val source: ST, // TODO: consider making this a var to allow for patterns in the abstract
    val destinationLabel: NodeLabel<DT>,
    val previous: Pattern<*,ST>? = null,
//    val dimension: String? = null // TODO: consider whether to use these abstract dimensions (could be an enum)
): Query() {
    // TODO: timecodes (or other additive values)
    // TODO: cascading properties

    fun warningNotImplemented(attributeName:String) =
        println("WARNING: '$attributeName' not implemented for {$this}")

    override val graphableNodes = sequence<GraphableNode> { warningNotImplemented("graphableNodes") }

    override operator fun <T: Node>invoke(label: NodeLabel<out T>): Sequence<T> {
        throw NotImplementedError("<T: Node>invoke not implemented for patterns")
    }

    override operator fun invoke(): Sequence<DT> = graphableNodes.map { destinationLabel.from(it) }

    open fun extend(vararg nodes: Node) = warningNotImplemented("extend")

    // deletes destinations (and all intermediary nodes/relationships)
    open fun deleteAll() {
        // should be overridden in if pattern logic includes intermediary nodes (in order to also delete intermediaries)
        graphableNodes.forEach {  source.context.graph.deleteNode(it.key) }
    }

    // deletes relationships (and potentially intermediary nodes), but not destinations
    open fun clear() = warningNotImplemented("clear")

    open fun stream(name:String, nodesLabel: NodeLabel<*>, vararg values: Any?) {
        val dimensionIterator = this().iterator()
        val valuesIterator = values.iterator()
        while (valuesIterator.hasNext()) {
            if (dimensionIterator.hasNext()) {
                dimensionIterator.next().apply {
                    properties[name] = valuesIterator.next()
                    save()
                }
            } else {
                extend(
                    nodesLabel.create(properties = mapOf(name to valuesIterator.next()) )
                )
            }
        }
    }

    // TODO: does this work??? Is it used? Naming?
    open fun setStream(name: String, vararg values:Any) {
        this().zip(values.asSequence()).forEach { it.first.properties[name] = it.second }
    }

    val cachedTarget get() = CachedTarget()

    // NOTE: doesn't actually cache, just mimics the sequence
    open inner class CachedTarget: TypedCached<DT>() {

        private var cachedNode = this.first

        var target: DT?
            get() = cachedNode
            set(node) {
                cachedNode = node
                clear()
                node?.let { extend(it) }
            }

        // TODO: implement
        fun createIfMissing(key:String = autoKey()) {
            if (cachedNode==null) {
                cachedNode = destinationLabel.create(key).also { extend(it) }
            }
        }
    }

}