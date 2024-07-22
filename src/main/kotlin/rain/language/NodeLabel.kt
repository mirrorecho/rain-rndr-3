package rain.language

import rain.graph.interfacing.*
import rain.patterns.Field
import rain.patterns.Message
import rain.patterns.nodes.Event
import rain.utils.autoKey
import kotlin.reflect.KClass



abstract class NodeLabel<T: Node>(
    parentLabel: NodeLabel<*>? = null,
    ): Queryable, Label<T>() {

    abstract val factory: (String)->T

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

    fun <RL:NodeLabel<*>>sends(
        receives:RL,
        key:String = autoKey(),
        block:RL.(message: Message<NodeLabel<T>, RL>)->Unit
    ): T {
        val message = Message<NodeLabel<T>, RL>(this, receives)
        block.invoke(receives, message)
        return this.create(key, message.properties)
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

    // TODO.. implement similar for merge above
    fun <MT : ManagerInterface> create(
        key: String = autoKey(),
        manager: MT,
        block: (MT.() -> Unit)? = null,
    ): T =
        factory(key).apply {
            block?.invoke(manager)
            this.updatePropertiesFrom(manager.properties)
            this.manageWith(manager) {}
            context.graph.create(this)
            manager.postCreate()
            registry[key] = this
        }

    fun <MT : ManagerInterface> sends(
        key: String,
        receivingManager: MT,
        block: (MT.() -> Unit)? = null,
    ): T =
        create<MT>(key, receivingManager, block)

    fun <MT : ManagerInterface> sends(
        receivingManager: MT,
        block: (MT.() -> Unit)? = null,
    ): T =
        sends(autoKey(), receivingManager, block)

    private fun registerMe() {
        context.nodeLabels[labelName] = this
    }


    // ============================================================

    fun getFields(vararg  fields: Field<Any, Node>): Map<String, Field<Any, Node>> =
        fields.associateBy { it.name }

    open val fields: Map<String, Field<Any, Node>> = mapOf()

    // ============================================================

    init {
        registerMe()
    }

}
