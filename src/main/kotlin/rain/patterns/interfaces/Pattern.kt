package rain.patterns.interfaces

import rain.language.SelectNodes
import rain.language.interfaces.LanguageNode
import rain.language.interfaces.NodeLabelInterface
import rain.patterns.nodes.PatternPlayer

// at its most basic level, a pattern represents:
// - necessarily, an origin node
// - properties, which may simply be
// - context relative to surrounding relationships in nodes, in some kind of defined "pattern"

// TYPES OF PATTERNS
// - any pattern can be simultaneous or not
// - simple destinations in order
// - destinations in random order
// - random x number of destinations
// - combine streams

enum class PatternDimension {
    CHILDREN,
    BUMPS,
    HISTORY;
}

interface Pattern {

    val node: LanguageNode
    val historyPattern: Pattern?
    val historyDimension: PatternDimension?

//    val dimensions: Map<Dimension, Sequence<Pattern>>
    val properties: MutableMap<String, Any?> get() = node.properties

    fun descendants(dimension: PatternDimension): Sequence<Pattern> = sequence {
        yield(this@Pattern)
        yieldAll(this@Pattern[dimension])
    }

    fun saveAll(dimension: PatternDimension) {
        descendants(dimension).forEach { it.node.save() }
    }

    operator fun get(dimension: PatternDimension): Sequence<Pattern> = when (dimension) {
        PatternDimension.HISTORY ->
            sequence { historyPattern?.let { yield(it); yieldAll(it[PatternDimension.HISTORY]) } }
        else -> sequence {
            println("WARNING: get not implemented for $dimension for ${this@Pattern}")
        }
    }


    operator fun invoke() { println("WARNING: invoke not implemented for $this") }
    fun play() = PatternPlayer(this).play()
    fun sendTo(dimension: PatternDimension, block: ((Pattern)->Unit)?=null) =
        this[dimension].forEach { it.receive(dimension, this); block?.invoke(it) }   // used for things like sending/triggering
    fun receive(dimension: PatternDimension, sender: Pattern) {
        when (dimension) {
            PatternDimension.BUMPS -> node.bump(this) // used for things triggering
        else -> println("WARNING: receive not implemented for $dimension for $this")
        }
    }

//    val isEmpty: Boolean get() = children.none()

    fun extend(dimension: PatternDimension, vararg nodes: LanguageNode) { println("WARNING: extend not implemented for $dimension  for $this") } // used for things like triggering

    fun selectFrom(nodeQuery:SelectNodes, dimension: PatternDimension): Sequence<Pattern> = selectAny(node[nodeQuery], dimension)

    fun selectAny(anyQuery:SelectNodes, dimension: PatternDimension): Sequence<Pattern> = sequence {
        anyQuery().forEach { n-> n.manager.makePattern(this@Pattern, dimension) }
    }

    // TODO: confirm this works!
    fun stream(dimension: PatternDimension, name:String, dimensionLabel: NodeLabelInterface<*>, vararg values: Any?) {
        val dimensionIterator = this@Pattern[dimension].iterator()
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
    fun setStream(dimension: PatternDimension, name: String, vararg values:Any) {
        this@Pattern[dimension].zip(values.asSequence()).forEach { it.first.node.properties[name] = it.second }
    }

}



