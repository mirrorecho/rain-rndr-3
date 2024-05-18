package rain.language.interfaces

import org.openrndr.Program
import rain.graph.interfaces.*
import rain.language.Manager
import rain.language.Relationship
import rain.language.RelationshipLabel
import rain.patterns.interfaces.Pattern
import rain.utils.autoKey

// TODO: this extra inheritance is odd here...
interface Labelable<T: LanguageItem> {
    val label: LabelInterface<out T>
}

interface LanguageItem: GraphableItem, Labelable<LanguageItem> {

    override val labels: List<String> get() = label.allNames

    override val labelName: String get() = label.labelName

    val context: Context get() = label.context

    val graph: GraphInterface get() = this.context.graph

}

// ===========================================================================================================

// TODO: try to clean up all these repetitive overrides
interface LanguageNode: LanguageItem, NodeSelectable, GraphableNode {
    override val label: NodeLabelInterface<out LanguageNode>

    override val labelName: String get() = label.labelName

    override val context: Context get() = label.context

    override val graph: GraphInterface get() = this.context.graph

//    val manager: ManagerInterface

    var manager: ManagerInterface

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
        targetNode: LanguageNode,
        key:String = autoKey()
    ): Relationship = rLabel.create(this.key, targetNode.key, key)

}

inline fun <T: ManagerInterface>LanguageNode.manageWith(manager:T, block: T.()->Unit): T {
    manager.manage(this)
    block(manager)
    return manager
}

inline fun LanguageNode.manage(block: (ManagerInterface.()->Unit)) = manageWith(manager, block)

// ===========================================================================================================


interface LanguageRelationship: LanguageItem, GraphableRelationship {
    fun save() = graph.save(this)

    fun read() = graph.read(this)

    fun delete() = graph.deleteRelationship(this.key)

//    fun mergeMe() = graph.merge(this) // TODO maybe: rename to merge?
//
//    fun createMe() = graph.create(this) // TODO maybe: rename to create?
}