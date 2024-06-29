package rain.language

import org.openrndr.Program
import rain.graph.interfacing.*
import rain.patterns.Dimension
import rain.patterns.Pattern
import rain.utils.autoKey
import rain.utils.lazyish
import kotlin.reflect.KProperty0

// ===========================================================================================================

abstract class Node protected constructor(
    key:String = autoKey(),
): Queryable, GraphableNode, Item(key) {
    //    companion object : NodeLabel<Node>(Node::class, null, { k->Node(k) })
    abstract override val label: NodeLabel<out Node>

    override val context get() = label.context

    override val queryMe get() = Query(selectKeys = arrayOf(this.key))

    // TODO maybe: consider moving this to the manager class?
    protected open val targetProperties:List<KProperty0<CachedTarget<out Node>>> = listOf()

    open var manager: ManagerInterface by lazyish { Manager() } // TODO: needed?

    open fun makePattern(historyDimension: Dimension?=null): Pattern =
        Pattern(this, historyDimension)

    open fun bump(vararg fromPatterns: Pattern) { println("invoke not implemented for $this") }

    open fun gate(onOff:Boolean=true)  { println("gate not implemented for $this") }

    open fun render(program: Program) { println("render not implemented for $this") }

    fun save() = context.graph.save(this)

    fun read() = context.graph.read(this)

    fun delete() {
        context.graph.deleteNode(this.key)
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

    fun <T: Node>cachedTarget(rLabel: RelationshipLabel, nLabel: NodeLabel<T>) =
        CachedTarget(this, rLabel, nLabel)

    fun getGraphableRelationships(relationshipLabelName:String, directionIsRight:Boolean=true) =
        context.graph.getRelationships(this.key, relationshipLabelName, directionIsRight)

    fun getRelationships(relationshipLabel:RelationshipLabel, directionIsRight:Boolean=true) =
        getGraphableRelationships(relationshipLabel.labelName, directionIsRight).map { relationshipLabel.from(it) }

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
