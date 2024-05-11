package rain.patterns.nodes

import rain.interfaces.*
import rain.language.SelectNodes
import rain.machines.nodes.Machine

// at its most basic level, a pattern represents:
// - necessarily, an origin node
// - properties, which may simply be
// - context relative to surrounding relationships in nodes, in some kind of defined "pattern"


interface PatternInterface {
    enum class Dimension {
        CHILDREN,
        BUMPS,
        HISTORY;
    }

    val node: LanguageNode
    val historyPattern: PatternInterface?
    val historyDimension: Dimension?

//    val dimensions: Map<Dimension, Sequence<Pattern>>
    val properties: MutableMap<String, Any?> get() = node.properties

    fun descendants(dimension: Dimension): Sequence<PatternInterface> = sequence {
        yield(this@PatternInterface)
        yieldAll(this@PatternInterface[dimension])
    }

    fun saveAll(dimension: Dimension) {
        descendants(dimension).forEach { it.node.save() }
    }

    operator fun get(dimension: Dimension): Sequence<PatternInterface> = when (dimension) {
        Dimension.HISTORY ->
            sequence { historyPattern?.let { yield(it); yieldAll(it[Dimension.HISTORY]) } }
        else -> sequence {
            println("WARNING: get not implemented for $dimension for ${this@PatternInterface}")
        }
    }


    operator fun invoke() { println("WARNING: invoke not implemented for $this") }
    fun play() = PatternPlayer(this).play()
    fun sendTo(dimension: Dimension) = this[dimension].forEach { it.receive(dimension, this) }   // used for things like sending/triggering
    fun receive(dimension: Dimension, sender:PatternInterface) {
        when (dimension) {
            Dimension.BUMPS -> node.bump(this) // used for things triggering
        else -> println("WARNING: receive not implemented for $dimension for $this")
        }
    }

//    val isEmpty: Boolean get() = children.none()

    fun extend(dimension: Dimension, vararg nodes:LanguageNode) { println("WARNING: extend not implemented for $dimension  for $this") } // used for things like triggering

    fun selectFrom(nodeQuery:SelectNodes, dimension: Dimension): Sequence<PatternInterface> = selectAny(node[nodeQuery], dimension)

    fun selectAny(anyQuery:SelectNodes, dimension: Dimension): Sequence<PatternInterface> = sequence {
        anyQuery().forEach { n-> n.manager.makePattern(this@PatternInterface, dimension) }
    }

    // TODO: confirm this works!
    fun stream(dimension: Dimension, name:String, dimensionLabel: NodeLabelInterface<*>, vararg values: Any?) {
        val dimensionIterator = this@PatternInterface[dimension].iterator()
        val valuesIterator = values.iterator()
        while (valuesIterator.hasNext()) {
            if (dimensionIterator.hasNext()) {
                dimensionIterator.next().apply {
                    node.properties[name] = valuesIterator.next()
                    node.save()
                }
            } else {
                extend(
                    dimension,
                    dimensionLabel.create(properties = mapOf(name to valuesIterator.next()) )
                )
            }
        }
    }

    // TODO: does this work??? Is it used? Naming?
    fun setStream(dimension: Dimension, name: String, vararg values:Any) {
        this@PatternInterface[dimension].zip(values.asSequence()).forEach { it.first.node.properties[name] = it.second }
    }

}

open class Pattern(
    override val node: LanguageNode,
    override val historyPattern: PatternInterface? = null,
    override val historyDimension: PatternInterface.Dimension? = null,
    ): PatternInterface

//open class MachinePattern(
//    override val node: LanguageNode,
//    override val historyPattern: PatternInterface? = null,
//    override val historyDimension: PatternInterface.Dimension? = null,
//): PatternInterface {
//    override fun receive(dimension: PatternInterface.Dimension, sender: PatternInterface) {
//        node.bump(dimension: Dimension,sender)
//    }
//}


//
//interface Pattern {
//
//    // TODO: caching?
//    val node: LanguageNode
//    val parents: Sequence<Pattern>
//    val children: Sequence<Pattern>
//    val receiver: Pattern
//
////    fun <T:Node>receiver(label: NodeLabel<out T>): T? = null // TODO: make this a sequence? (to allow for multiple triggers?)
//
//    val descendants: Sequence<Pattern> get() = sequence {
//        yield(this@Pattern)
//        yieldAll(this@Pattern.children)
//    }
//
//    fun saveAll() {
//        descendants.forEach { it.node.save() }
//    }
//
//    val properties: MutableMap<String, Any?> get() = node.properties
//
//    operator fun invoke() { println("WARNING: invoke not implemented for $this") } // used for things like sending/triggering
//    fun play() = PatternPlayer(this).play()
//    fun send() = receiver.receive(this)  // used for things like sending/triggering
//    fun receive(sender:Pattern) { println("WARNING: receive not implemented for $this") } // used for things like sending/triggering
//
//    val isEmpty: Boolean get() = children.none()
//
//    fun extend(vararg nodes:LanguageNode) { println("WARNING: extend not implemented for $this") } // used for things like triggering
//
//
//    // TODO: confirm this works!
//    fun stream(name:String, childLabel: NodeLabelInterface<*>, vararg values: Any?) {
//        val childrenIterator = children.iterator()
//        val valuesIterator = values.iterator()
//        while (valuesIterator.hasNext()) {
//            if (childrenIterator.hasNext()) {
//                childrenIterator.next().apply {
//                    node.properties[name] = valuesIterator.next()
//                    node.save()
//                }
//            } else {
//                extend(
//                    childLabel.create(properties = mapOf(name to valuesIterator.next()) )
//                )
//            }
//        }
//    }
//
//    // TODO: does this work??? Is it used? Naming?
//    fun setVein(name: String, vararg values:Any) {
//        children.zip(values.asSequence()).forEach { it.first.node.properties[name] = it.second }
//    }
//
//}



// TYPES OF PATTERNS
// - any pattern can be simultaneous or not
// - simple destinations in order
// - destinations in random order
// - random x number of destinations
// - combine streams


