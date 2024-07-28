package rain.language

import rain.graph.interfacing.*
import rain.utils.autoKey
import kotlin.reflect.KClass





abstract class NodeLabel<T: Node>(
    parentLabel: NodeLabel<*>? = null,
    ): Queryable, Label<T>() {

    abstract fun factory(key:String): T

    private fun getName(cl:KClass<T>) = cl.simpleName ?: "Node"

    // TODO: needed?
//    override val ancestorLabels: List<NodeLabel<*>> = parentLabel?.let { listOf(it) + it.ancestorLabels }.orEmpty()

    final override val allNames: List<String> = listOf(getName(myClass)) + parentLabel?.allNames.orEmpty()

    final override val queryMe: Query = Query(selectLabelName=labelName)

    operator fun get(vararg keys:String) = Query(selectKeys=keys)

    // TODO: review, then delete
//    open val receives: Manager get() = Manager()

    val registry: MutableMap<String, T> = mutableMapOf()

    override fun toString() = labelName

    fun <R:Node, RL:NodeLabel<R>>sends(
        receives:RL,
        key:String = autoKey(),
        preCreate:RL.(Message<R, RL>)->Unit,
        postCreate:RL.(T)->Unit,
    ): T {
        val message = Message(receives)
        preCreate.invoke(receives, message)
        return this.create(key, message.properties).apply {
            postCreate.invoke(receives, this)
        }
    }


    fun get(key: String): T =
        registry.getOrPut(key) {
            factory(key).apply {
                context.graph.read(this)
            }
        }

    fun from(gNode: GraphableNode): T =
        registry.getOrPut(gNode.key) {
            factory(gNode.key).apply {
                updatePropertiesFrom(gNode)
            }
        }

    fun merge(
        key: String = autoKey(),
        properties: Map<String, Any?>? = null,
    ): T =
        registry.getOrPut(key) { factory(key) }.also { node ->
            properties?.let { node.updatePropertiesFrom(it) };
            context.graph.merge(node)
        }

    fun create(
        key: String = autoKey(),
        properties: Map<String, Any?>? = null,
    ): T =
        factory(key).apply {
            properties?.let { this.updatePropertiesFrom(it) }
            context.graph.create(this)
            registry[key] = this
        }

    //TODO: review, then delete
//    fun <MT : ManagerInterface> create(
//        key: String = autoKey(),
//        manager: MT,
//        block: (MT.() -> Unit)? = null,
//    ): T =
//        factory(key).apply {
//            block?.invoke(manager)
//            this.updatePropertiesFrom(manager.properties)
//            this.manageWith(manager) {}
//            context.graph.create(this)
//            manager.postCreate()
//            registry[key] = this
//        }

    //TODO: review, then delete
//    fun <MT : ManagerInterface> sends(
//        key: String,
//        receivingManager: MT,
//        block: (MT.() -> Unit)? = null,
//    ): T =
//        create<MT>(key, receivingManager, block)
//
//    fun <MT : ManagerInterface> sends(
//        receivingManager: MT,
//        block: (MT.() -> Unit)? = null,
//    ): T =
//        sends(autoKey(), receivingManager, block)

    private fun registerMe() {
        context.nodeLabels[labelName] = this
    }


    // ============================================================

    fun getFields(vararg  fields: Field<*>): Map<String, Field<*>> =
        fields.associateBy { it.name }

    open val fields: Map<String, Field<*>> = mapOf()

    // ============================================================

    init {
        registerMe()
    }

}

// TODO: move all these to some context?

// TODO: naming OK (same as name within NodeLabel)?
fun <R:Node, RL:NodeLabel<R>>RL.merge(
    key: String = autoKey(),
    messageBlock: (RL.(Message<R, RL>)->Unit)?=null,
): R {
    messageBlock?.let { mb ->
        return this.merge(
            key,
            Message(this).also { msg-> mb.invoke(this, msg) }.properties
        )
    }
    return this.merge(key)
}

fun <R:Node, RL:NodeLabel<R>>RL.create(
    key: String = autoKey(),
    messageBlock: (RL.(Message<R, RL>)->Unit)?=null,
): R {
    messageBlock?.let { mb ->
        return this.create(
            key,
            Message(this).also { msg-> mb.invoke(this, msg) }.properties
        )
    }
    return this.create(key)
}

//fun field<T:Any>


fun <N:Node, FT:Any, R:Node, RL:NodeLabel<R>>N.relateField(
    field: Field<FT>,
    relatedLabel: RL,
    fieldName:String,
    key: String = autoKey(),
    messageBlock: (RL.(Message<R, RL>)->Unit)?=null,
    postCreate:RL.(R)->Unit,
) {
    // TODO: complete this...
    val relatedNode = relatedLabel.merge(key, messageBlock)
    // TODO: add fieldName to the relationship
    this.relate(field.relationshipLabel!!, relatedNode) // TODO: guarantee that relationshipLabel not null
}