package rain.language

import org.openrndr.Program
import rain.graph.interfacing.*
import rain.patterns.Pattern
import rain.patterns.nodes.Machine
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

    // TODO: are these needed at the node level? or only the machine level?
//    open fun gate(onOff:Boolean=true)  { println("gate not implemented for $this") }
//
//    open fun render(program: Program) { println("render not implemented for $this") }

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

    // a managed map of attached ContectedField objects, for mass connecting them
    // TODO maybe: should this just be a list? do we ever need to look up by field name?
    val attachedFields: MutableMap<String, AttachedField<Any?>> = mutableMapOf()

    fun connectAllFields() {
        attachedFields.forEach { (_, v) -> v.connect() }
    }


    // TODO: consider implementing
//    open fun bump(vararg fromPatterns: Pattern) { println("invoke not implemented for $this") }


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

}

// ================================================================

// just for fiddling around purposes...
open class Thingy protected constructor(
    key:String = autoKey(),
): Node(key) {
    abstract class ThingyLabel<T: Thingy>: NodeLabel<T>() {
        // add fields here:
        val thing = field("thing", "One and Two")
    }

    companion object : ThingyLabel<Thingy>() {
        override val labelName:String = "Thingy"
        override fun factory(key:String) = Thingy(key)
    }

    override val label: NodeLabel<out Thingy> = Thingy

    // attach fields here:
    val thing = attachField(Thingy.thing)

}
