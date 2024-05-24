package rain.patterns.interfaces
import rain.language.Node
import rain.language.NodeQuery
import rain.language.interfacing.*

typealias DimensionFactory = (Pattern) -> Dimension
//typealias PatternFactory = (LanguageNode) -> Pattern

//inline fun Dimension.forEach(block: (Pattern)->Unit) = invoke().forEach(block)

enum class DimensionLabel {
    CHILDREN,
    TRIGGERS,
//    BUMPS,
    HISTORY;
}

interface DimensionCompanion {
    val label: DimensionLabel
    val factory: DimensionFactory
}

// TODO: consider making this interface itself (and maybe select), an implementation of a Sequence
abstract class Dimension(
    val pattern:Pattern,
    open val label:DimensionLabel,
): NodeQuery() {

    override val queryMe get() = this

    override fun asPatterns(): Sequence<Pattern> = this().map { it.makePattern(this) }

    fun warningNotImplemented(attributeName:String) =
        println("WARNING: '$attributeName' not implemented for {$this@PatternDimension2} on ${this.pattern}")

    val isEmpty: Boolean get() = invoke().none()


    val descendantPatterns: Sequence<Pattern> get() = sequence {
        this@Dimension.asPatterns().forEach {
            yield(it)
            yieldAll(it[label].asPatterns())
        }
    }


    fun saveAll() {
        descendantPatterns.forEach { it.node.save() }
    }

    open fun extend(vararg nodes: Node) = warningNotImplemented("extend")

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

    // for now, KISS
//    fun sendTo(block: ((Pattern)->Unit)?=null) =
//        forEach { it.receive(dimension, this); block?.invoke(it) }   // used for things like sending/triggering
//
//    fun receive(sender: Pattern) = warningNotImplemented("invoke")

//    fun connectReceiver(dimension: DimensionLabel, receiver:LanguageNode) = warningNotImplemented("connectReceiver")

//    fun makeReceiver(dimension: DimensionLabel, receiverLabel: NodeLabel<*>) = warningNotImplemented("makeReceiver")

    //    //selects from the current pattern's node
//    fun selectFrom(nodeQuery: SelectNodes): Sequence<Pattern> = selectAny(pattern.node[nodeQuery])
//
//    //selects anywhere
//    fun selectAny(anyQuery: SelectNodes): Sequence<Pattern> =
//        anyQuery().map { n-> n.makePattern(this) }


}

