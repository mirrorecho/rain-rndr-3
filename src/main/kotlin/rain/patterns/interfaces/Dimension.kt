package rain.patterns.interfaces
import rain.language.interfaces.*

import rain.language.NodeLabel
import rain.language.SelectNodes

typealias DimensionFactory = (Pattern) -> Dimension
typealias PatternFactory = (LanguageNode) -> Pattern

inline fun Dimension.forEach(block: (Pattern)->Unit) = invoke().forEach(block)

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
interface Dimension {
    val pattern:Pattern
    val label:DimensionLabel

//    fun makeHistory(node:LanguageNode) = Pattern(node, pattern, label)

//    fun makeHistoryCopyingDimensions(node:LanguageNode): Pattern {
//        return Pattern(node, pattern, label).apply {
//            add(*(this@Dimension.pattern.dimensions.map { it.copy(this) }.toTypedArray()))
//        }
//    }

    fun copy(anotherPattern: Pattern): Dimension

    fun warningNotImplemented(attributeName:String) =
        println("WARNING: '$attributeName' not implemented for {$this@PatternDimension2} on ${this.pattern}")

    operator fun invoke(): Sequence<Pattern> = sequence {
        warningNotImplemented("invoke")
    }

    val isEmpty: Boolean get() = invoke().none()


//    fun forEach(block: (Pattern)->Unit) = invoke().forEach(block)

    val descendants: Sequence<Pattern> get() = sequence {
        this@Dimension().forEach {
            yield(it)
            yieldAll(it[label]())
        }
    }

    // for now, KISS
//    fun sendTo(block: ((Pattern)->Unit)?=null) =
//        forEach { it.receive(dimension, this); block?.invoke(it) }   // used for things like sending/triggering
//
//    fun receive(sender: Pattern) = warningNotImplemented("invoke")

    fun connectReceiver(dimension: DimensionLabel, receiver:LanguageNode) = warningNotImplemented("connectReceiver")

    fun makeReceiver(dimension: DimensionLabel, receiverLabel: NodeLabel<*>) = warningNotImplemented("makeReceiver")

    fun saveAll() {
        descendants.forEach { it.node.save() }
    }

    fun extend(vararg nodes: LanguageNode) = warningNotImplemented("extend")

    fun stream(name:String, nodesLabel: NodeLabelInterface<*>, vararg values: Any?) {
        val dimensionIterator = this().iterator()
        val valuesIterator = values.iterator()
        while (valuesIterator.hasNext()) {
            if (dimensionIterator.hasNext()) {
                dimensionIterator.next().apply {
                    node.properties[name] = valuesIterator.next()
                    node.save()
                }
            } else {
                extend(
                    nodesLabel.create(properties = mapOf(name to valuesIterator.next()) )
                )
            }
        }
    }

    // TODO: does this work??? Is it used? Naming?
    fun setStream(dimension: DimensionLabel, name: String, vararg values:Any) {
        this().zip(values.asSequence()).forEach { it.first.node.properties[name] = it.second }
    }

    //selects from the current pattern's node
    fun selectFrom(nodeQuery: SelectNodes): Sequence<Pattern> = selectAny(pattern.node[nodeQuery])

    //selects anywhere
    fun selectAny(anyQuery: SelectNodes): Sequence<Pattern> =
//        anyQuery().map { n-> makeHistory(n) }
        anyQuery().map { n-> n.makePattern(pattern, label) }

}

//// TODO: consider whether history should instead be implemented at the dimension level?
//class EmptyDimension(final override val pattern:Pattern): PatternDimensionBase {
//
//    override val dimensionEnum: PatternDimension = PatternDimension.HISTORY
//
//    override val patternFactory: PatternFactory = {n-> BasicPattern(n, pattern, dimensionEnum) }
//
//    override fun invoke(): Sequence<Pattern> =
//        sequence { pattern.historyPattern?.let { yield(it); yieldAll(it[PatternDimension.HISTORY]()) } }
//}


