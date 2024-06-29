package rain.language

import rain.graph.interfacing.*
import rain.utils.autoKey
import kotlin.reflect.KClass



abstract class NodeLabel<T: Node>(
    myClass: KClass<T>,
    parentLabel: NodeLabel<*>? = null,
    val factory: (String)->T,
    ): Queryable, Label<T>() {

    private fun getName(cl:KClass<T>) = cl.simpleName ?: "Node"

    final override val labelName:String = getName(myClass)

    // TODO: needed?
//    override val ancestorLabels: List<NodeLabel<*>> = parentLabel?.let { listOf(it) + it.ancestorLabels }.orEmpty()

    final override val allNames: List<String> = listOf(getName(myClass)) + parentLabel?.allNames.orEmpty()

    final override val queryMe: Query = Query(selectLabelName=labelName)

    operator fun get(vararg keys:String) = Query(selectKeys=keys)

    open val receives: Manager get() = Manager()

    val registry: MutableMap<String, T> = mutableMapOf()

    override fun toString() = labelName

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


    init {
        registerMe()
    }

}
