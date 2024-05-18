package rain.language

import rain.language.interfaces.LanguageNode
import rain.language.interfaces.ManagerInterface
import rain.language.interfaces.SelectDirection
import rain.patterns.interfaces.Pattern
import rain.patterns.nodes.Event
import rain.utils.autoKey
import rain.utils.lazyish
import kotlin.reflect.KProperty0

// TODO: consider making Node abstract?
open class Node protected constructor(
    key:String = autoKey(),
): LanguageNode, Item(key) {
    companion object : NodeLabel<Node>(Node::class, null, {k->Node(k) })
    override val label:NodeLabel<out Node> = Node

    override val context get() = label.context
    override val graph get() = context.graph

    override val selectMe get() = SelectNodes(keys=listOf(this.key))
    override val labelName get() = label.labelName

    // TODO maybe: consider moving this to the manager class?
    protected open val targetProperties:List<KProperty0<CachedTarget<out Node>>> = listOf()

    override var manager: ManagerInterface by lazyish { Manager() }

//    override val manager by lazy { this.manageWith(Manager()) }
//
////    override fun getPattern(previous: Pattern?): Pattern = SelfPattern(this)
////    override val manager by lazy {  NodeManager(this) }
//
//    private var myManager: ManagerInterface? = null
////    override var manager get() = myManager ?: Manager()
////        set(manager:ManagerInterface) {this.myManager = manager}

    fun autoTarget() {
        targetProperties.forEach {
            it.get().apply {
                createIfMissing()
                target?.autoTarget() // cascade down...
            }
        }
    }

    fun <T:Node>cachedTarget(rLabel: RelationshipLabel, nLabel: NodeLabel<T>, direction: SelectDirection = SelectDirection.RIGHT) =
        CachedTarget(this, rLabel, nLabel, direction)

    // TODO: maybe implement this...?
//    fun <T:Node>targetsOrMake(
//        rLabel:RelationshipLabel, // TODO: add default label
//        nLabel:NodeLabel<T>,
//        targetKey: String = autoKey()
//    ): T {
//        this[rLabel()](nLabel).firstOrNull()?.let { return it }
//        return nLabel.merge(targetKey).also { this.relate(rLabel, it) }
//    }

    // TODO: maybe implement this...?
//    fun invoke()

}

// ===========================================================================================================

// just for fiddling around purposes...
open class SpecialNode protected constructor(
    key:String = autoKey(),
): Node(key) {
    companion object : NodeLabel<SpecialNode>(SpecialNode::class, Node, { k -> SpecialNode(k) })
    override val label: NodeLabel<SpecialNode> = SpecialNode

    class SpecialNodeManager : Manager() {
        var special: String? by properties
    }
//    override val manager by lazy { SpecialNodeManager().apply { manage(this@SpecialNode) } }

}
