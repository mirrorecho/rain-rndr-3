package rain.language

import org.openrndr.Program
import rain.graph.interfacing.*
import rain.patterns.Pattern
import rain.utils.autoKey
import rain.utils.lazyish

// ===========================================================================================================

abstract class Node protected constructor(
    key:String = autoKey(),
): Queryable, GraphableNode, Item(key) {
    //    companion object : NodeLabel<Node>(Node::class, null, { k->Node(k) })
    abstract override val label: NodeLabel<out Node>

    override val context get() = label.context

    override val queryMe get() = Query(selectKeys = arrayOf(this.key))

    open var manager: ManagerInterface by lazyish { Manager() } // TODO: needed?

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

    fun getGraphableRelationships(relationshipLabelName:String, directionIsRight:Boolean=true) =
        context.graph.getRelationships(this.key, relationshipLabelName, directionIsRight)

    fun getRelationships(relationshipLabel:RelationshipLabel, directionIsRight:Boolean=true) =
        getGraphableRelationships(relationshipLabel.labelName, directionIsRight).map { relationshipLabel.from(it) }

    fun <T:Any, NT:Node>fieldValue(field: Field<T, NT>): Pattern<NT>.CachedTarget.FieldValue<T> {
        return field.cachedFieldValue()
    }

    // TODO: consider re-implementing
    // TODO maybe if so: consider moving this to the manager class?
//    protected open val targetProperties:List<KProperty0<CachedTarget<out Node>>> = listOf()

    // TODO: consider re-implementing (does this even make sense?)
//    open fun makePattern(historyDimension: Dimension?=null): Pattern =
//        Pattern(this, historyDimension)

    // TODO: consider implementing
//    open fun bump(vararg fromPatterns: Pattern) { println("invoke not implemented for $this") }

    // TODO: consider re-implementing
//    fun autoTarget() {
//        targetProperties.forEach {
//            it.get().apply {
//                createIfMissing()
//                target?.autoTarget() // cascade down...
//            }
//        }
//    }

    // TODO: consider re-implementing
//    fun <T: Node>cachedTarget(rLabel: RelationshipLabel, nLabel: NodeLabel<T>): Pattern.CachedTarget =
//        CachedTarget(this, rLabel, nLabel)

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

    abstract val message: Message<out Node, out NodeLabel<out Node>>


    open fun wireUp() {
        message.properties = this.properties
    }

}

// TODO maybe: re-implement?
//inline fun <T: ManagerInterface> Node.manageWith(manager:T, block: T.()->Unit): T {
//    manager.manage(this)
//    block(manager)
//    return manager
//}
//
//inline fun Node.manage(block: (ManagerInterface.()->Unit)) = manageWith(manager, block)

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
