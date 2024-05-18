package rain.language.interfaces

import rain.graph.interfaces.*
import rain.utils.autoKey

interface LabelInterface<T: LanguageItem> {
    val labelName:String
    val isRoot: Boolean
    val isRelationship:Boolean
    var context: Context
    val allNames: List<String>
}

interface NodeLabelInterface<T: LanguageNode>: LabelInterface<T> {
    // TODO: needed?
    //    val ancestorLabels: List<NodeLabelInterface<*>>

    override val isRelationship: Boolean get() = false

    val factory: (String) -> T

    val registry: MutableMap<String, T>

    val receives: ManagerInterface

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

}


interface RelationshipLabelInterface<T: LanguageRelationship>: LabelInterface<T> {
    override val isRelationship: Boolean get() = true
    override val isRoot: Boolean get() = false

    fun factory(sourceKey:String, targetKey:String, key:String = autoKey() ): T

    fun from(gRelationship: GraphableRelationship):T = gRelationship.run {
        factory(source.key, target.key, key).also { it.updatePropertiesFrom(this) }
    }

    // TODO: is all this complexity necessary for relationships???

    fun merge(
        sourceKey:String,
        targetKey:String,
        key:String,
        properties:Map<String,Any?>?=null,
        blockBefore:( T.()->Unit )?=null,
        blockAfter:( T.()->Unit )?=null
    ):T =
        factory(sourceKey, targetKey, key).apply {
            properties?.let{ this.properties.putAll(it) };
            blockBefore?.invoke(this);
            context.graph.merge(this);
            blockAfter?.invoke(this);
        }

    fun create(
        sourceKey:String,
        targetKey:String,
        key:String = autoKey(),
        properties:Map<String,Any?>?=null,
        blockBefore:( T.()->Unit )?=null,
        blockAfter:( T.()->Unit )?=null
    ):T =
        factory(sourceKey, targetKey, key).apply {
            properties?.let{ this.properties.putAll(it) };
            blockBefore?.invoke(this);
            context.graph.create(this);
            blockAfter?.invoke(this);
        }

}