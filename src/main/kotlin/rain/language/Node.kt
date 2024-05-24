package rain.language

import org.openrndr.Program
import rain.graph.interfaceable.GraphableNode
import rain.language.interfacing.ManagerInterface
import rain.language._bak2.SelectDirection
import rain.language.interfacing.NodeLabel
import rain.language.interfacing.queries.NodeQueryable
import rain.patterns.interfaces.Dimension
import rain.patterns.interfaces.Pattern
import rain.utils.autoKey
import rain.utils.lazyish
import kotlin.reflect.KProperty0

// ===========================================================================================================

abstract class Node protected constructor(
    key:String = autoKey(),
): NodeQueryable, GraphableNode, Item(key) {
    //    companion object : NodeLabel<Node>(Node::class, null, { k->Node(k) })
    abstract override val label: NodeLabel<out Node>

    override val context get() = label.context

    override val queryMe get() = SelectNodes(keys=listOf(this.key))

    // TODO maybe: consider moving this to the manager class?
    protected open val targetProperties:List<KProperty0<CachedTarget<out Node>>> = listOf()

    var manager: ManagerInterface by lazyish { Manager() } // TODO: needed?

//    val manager: ManagerInterface

    fun makePattern(historyDimension: Dimension?=null): Pattern =
        Pattern(this, historyDimension)

    fun bump(vararg fromPatterns: Pattern) { println("invoke not implemented for $this") }

    fun gate(onOff:Boolean=true)  { println("gate not implemented for $this") }

    fun render(program: Program) { println("render not implemented for $this") }

    fun save() = graph.save(this)

    fun read() = graph.read(this)

    fun delete() {
        graph.deleteNode(this.key)
        label.registry.remove(this.key)
    }

    fun relate(
        rLabel: RelationshipLabel,
        targetKey:String,
        key:String = autoKey()
    ): Relationship = rLabel.create(this.key, targetKey, key)

    fun relate(
        rLabel: RelationshipLabel,
        targetNode: Node,
        key:String = autoKey()
    ): Relationship = rLabel.create(this.key, targetNode.key, key)

    fun autoTarget() {
        targetProperties.forEach {
            it.get().apply {
                createIfMissing()
                target?.autoTarget() // cascade down...
            }
        }
    }

    fun <T: Node>cachedTarget(rLabel: RelationshipLabel, nLabel: NodeLabel<T>, direction: SelectDirection = SelectDirection.RIGHT) =
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

inline fun <T: ManagerInterface> Node.manageWith(manager:T, block: T.()->Unit): T {
    manager.manage(this)
    block(manager)
    return manager
}

inline fun Node.manage(block: (ManagerInterface.()->Unit)) = manageWith(manager, block)

// just for fiddling around purposes...
open class Thingy protected constructor(
    key:String = autoKey(),
): Node(key) {
    companion object : NodeLabel<Thingy>(Thingy::class, null, { k -> Thingy(k) })
    override val label: NodeLabel<Thingy> = Thingy

    class ThingyManager : Manager() {
        var thingName: String? by properties
    }
//    override val manager by lazy { ThingyManager().apply { manage(this@Thingy) } }

}
