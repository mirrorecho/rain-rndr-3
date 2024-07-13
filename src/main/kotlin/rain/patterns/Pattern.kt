package rain.patterns

import rain.graph.interfacing.GraphableNode
import rain.graph.interfacing.QueryMethod
import rain.language.*
import rain.utils.autoKey
import kotlin.reflect.KProperty

// patterns are abstractions of queries
abstract class Pattern<T:Node, out ST:T, DT:T>( // TODO: consider whether the generic ty[es are worth it, otherwise KISS!

    // TODO: consider making this a var to allow for patterns in the abstract
    // TODO: also, is source worthwhile here, or just override query's directly, OR, just make this a arg, not a var
    val source: ST,
    val destinationLabel: NodeLabel<DT>,
    val previous: Pattern<T, *, out ST>? = null,
//    val dimension: String? = null // TODO: consider whether to use these abstract dimensions (could be an enum)
): Query( QueryMethod.GRAPHABLE) {
    // TODO: cascading properties
    // TODO: cascading target/context node(s) ... i.e. for Machine target in an Event tree
    // TODO: timecodes (or other additive values)

    fun warningNotImplemented(attributeName:String) =
        println("WARNING: '$attributeName' not implemented for {$this}")

    // ------------------------------------------------------------------

    // graphableNodes MUST be overridden, as it defines the logic of the query
    override abstract val graphableNodes: Sequence<GraphableNode>

    // extend MAY be overridden
    open fun extend(vararg nodes: Node) = warningNotImplemented("extend")

    // deletes relationships and potentially intermediary nodes (and destination nodes if deleteNodes=true)
    open fun clear(deleteNodes:Boolean=false) = warningNotImplemented("clear")

    override operator fun <T: Node>invoke(label: NodeLabel<out T>): Sequence<T> {
        throw NotImplementedError("<T: Node>invoke not implemented for patterns")
    }

    override var queryFrom: Query? = source.queryMe

    override operator fun invoke(): Sequence<DT> = graphableNodes.map { destinationLabel.from(it) }

    // TODO: this works great... so make sure I understand EXACTLY what's going on
    //  ... ALSO, consider moving to Query to use on non-patterns?
    open fun <DT2:T, P:Pattern<T, DT, DT2>>asPatterns(
        destinationLabel: NodeLabel<DT2>,
        factory:(source:DT, destinationLabel: NodeLabel<DT2>, previous:Pattern<T, ST,DT>)->P
    ): Sequence<P> = this().map { factory.invoke(it, destinationLabel, this) }

    // ------------------------------------------------------------------

    // TODO: Node Type OK here?
//    val history: HistoryPattern<ST, PT, *> get() = HistoryPattern<ST, PT, Node>(source, )

    val history: Sequence<Pattern<T, *, *>> = sequence {
        previous?.let { yield(it); yieldAll(it.history) }
    }

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

    // TODO: is the below note correct? Or or holdover from sandbox?
    // NOTE: doesn't actually cache, just mimics the sequence
    inner class CachedTarget: TypedCached<DT>() {

        private var cachedNode = this.first

        val sourcePattern get() = this@Pattern
        val sourceNode get() = sourcePattern.source

        var target: DT?
            get() = cachedNode
            set(node) {
                cachedNode = node
                clear()
                node?.let { extend(it) }
            }

        // TODO: is this used?
        fun createIfMissing(key:String = autoKey()) {
            if (cachedNode==null) {
                cachedNode = destinationLabel.create(key).also { extend(it) }
            }
        }

        inner class FieldValue<T:Any>(
            val name:String,
        ) {
            // TODO: implement defaults, caching
            //  TODO maybe: ANIMATION????

            operator fun getValue(thisRef: Any?, property: KProperty<*>): T? =
                this@CachedTarget.target?.properties?.get(name) as T?

            operator fun setValue(thisRef: Any?, property: KProperty<*>, value:T) {
                this@CachedTarget.target!!.properties[name] = value
            }
        }

    }


}

