package rain.patterns.interfaces

import rain.language.Node
import rain.language.interfacing.ManagerInterface
import rain.patterns.nodes.HistoryDimension

// at its most basic level, a pattern represents:
// - necessarily, an origin node
// - properties, which may simply be
// - dimensions relative to surrounding relationships in nodes, in some kind of defined "pattern"

// TYPES OF PATTERNS
// - simple destinations in order
// - destinations in random order
// - random x number of destinations
// - combine streams


class Pattern(
    val node: Node,
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

    val cascadingProperties: MutableMap<String, Any?> by lazy {
        historyDimension?.pattern?.cascadingProperties.orEmpty().toMutableMap().apply { putAll(node.properties) }
    }

}

inline fun <T: ManagerInterface>Pattern.manageWith(manager:T, block: T.()->Unit): T {
    manager.manage(this)
    block(manager)
    return manager
}

inline fun Pattern.manage(block: (ManagerInterface.()->Unit)) = manageWith(node.manager, block)
