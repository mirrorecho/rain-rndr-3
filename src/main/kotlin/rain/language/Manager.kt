package rain.language

import rain.language.interfacing.*
import rain.patterns.interfaces.*
import rain.utils.lazyish
import kotlin.reflect.KProperty

class DefaultableManager<T>(val manager: ManagerInterface, name:String, default:T) {

    // TODO: is this the right logic here, or should we check for key existence instead of null check?
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = manager.properties[property.name] as T

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value:T) {manager.properties[property.name] = value}

    init {
        default?.let { manager.properties[name] = it }
    }

}


open class Manager: ManagerInterface {

    fun <T>defaultable(name: String, default:T) = DefaultableManager(this, name, default)
    fun <T>nullable(name: String, default:T?=null) = DefaultableManager(this, name, default)

    final override var properties: MutableMap<String, Any?> = mutableMapOf()

    final override val node by lazyish { myNode }
    override var pattern: Pattern? by lazyish { node?.makePattern() }

    private var myNode: Node? = null

    override var extendDimension = DimensionLabel.CHILDREN

    override var deferredBlocks = mutableListOf<((Pattern)->Unit)>()

    override fun manage(properties: MutableMap<String, Any?>) {this.properties = properties}
    override fun manage(node: Node) {node.manager = this; myNode = node;  manage(node.properties)}
    override fun manage(pattern: Pattern) {this.pattern=pattern; manage(pattern.node)}

}
