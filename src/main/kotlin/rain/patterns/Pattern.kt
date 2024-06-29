package rain.patterns

import rain.language.Node
import rain.language.ManagerInterface
import rain.utils.lazyish
import kotlin.reflect.KProperty

// at its most basic level, a pattern represents:
// - a set of named relationship paths between some source node and destination nodes/patterns
// - properties, which may simply be the nodes properties
// - dimensions relative to surrounding relationships in nodes, in some kind of defined "pattern"

// TYPES OF PATTERNS
// - simple destinations in order
// - destinations in random order
// - random x number of destinations
// - combine streams

open class MessageSpace(
    val pattern: Pattern
) {

}

class CircleMessageSpace(pattern: Pattern): MessageSpace(pattern=pattern) {
    var dur: Double by this.pattern
}


// TODO: node can be Null!
class Pattern(
    val node: Node? = null,
    val historyDimension: Dimension?,
    vararg dimensions: DimensionCompanion,
){
    constructor(
        node: Node,
        vararg dimensions: DimensionCompanion
    ) : this(node, null,  *dimensions)

    override fun toString():String = "Pattern with dimensions ${dimensions.map { it.label }}"

    private val myDimensions: MutableMap<DimensionLabel, Dimension> =
        dimensions.associate { it.label to it.factory(this) }.toMutableMap()

    val history by lazy { HistoryDimension(this) }

    val dimensions get() = myDimensions.values.toTypedArray()

    operator fun <T>getValue(thisRef: Any?, property: KProperty<*>): T = cascadingProperties[property.name] as T

    operator fun <T>setValue(thisRef: Any?, property: KProperty<*>, value:T) {map[property.name] = value}

    operator fun get(label: DimensionLabel): Dimension = myDimensions[label]!!

    fun add(vararg dimensions: Dimension) = this.apply { myDimensions.putAll(dimensions.associateBy { it.label }) }

    fun add(vararg dimensionFactories: DimensionFactory) = add(*(dimensionFactories.map{ it(this) }.toTypedArray()))

    fun add(vararg dimensions: DimensionCompanion) = add(*(dimensions.map{ it.factory }.toTypedArray()))

    // maybe bring these back...
    //    operator fun set(label: DimensionLabel, dimension: DimensionCompanion) = set(label, dimension.factory)
    //
    //    fun set(label: DimensionLabel, dimensionFactory: DimensionFactory)
    //    { this.myDimensions[label] = dimensionFactory(this) }


    val labels: Set<DimensionLabel> get() = myDimensions.keys

    // TODO: does this work with lazyish!!!!?
    val cascadingProperties: MutableMap<String, Any?> by lazyish {
        historyDimension?.pattern?.cascadingProperties.orEmpty().toMutableMap().apply { putAll(node?.properties.orEmpty()) }
    }

}

inline fun <T: ManagerInterface> Pattern.manageWith(manager:T, block: T.()->Unit): T {
    manager.manage(this)
    block(manager)
    return manager
}


//inline fun Pattern.manage(block: (ManagerInterface.()->Unit)) = manageWith(node.manager, block)
