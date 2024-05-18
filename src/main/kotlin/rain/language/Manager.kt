package rain.language

import rain.language.interfaces.*
import rain.patterns.interfaces.*
import rain.utils.DefaultableMap
import rain.utils.lazyish
import rain.utils.withDefault
import rain.utils.withNull
import kotlin.reflect.KProperty

class DefaultableManager<T>(val manager: ManagerInterface, val default:T) {

    // TODO: is this the right logic here, or should we check for key existence instead of null check?
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = manager.properties.getOrDefault(property.name, default) as T

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value:T) {manager.properties[property.name] = value}

}


open class Manager: ManagerInterface {

    fun <T>defaultable(default:T) = DefaultableManager(this, default)
    fun <T>nullable(default:T?=null) = DefaultableManager(this, default)

    final override var properties: MutableMap<String, Any?> = mutableMapOf()

    final override val node by lazyish { myNode }
    override var pattern: Pattern? by lazyish { node?.let { Pattern(it) } }

    private var myNode: LanguageNode? = null

    override var extendDimension = DimensionLabel.CHILDREN

    override var deferredBlocks = mutableListOf<((Pattern)->Unit)>()

    // TODO: this doesn't work...!!!
    override fun manage(properties: MutableMap<String, Any?>) {this.properties = properties}
    override fun manage(node: LanguageNode) {node.manager = this; myNode = node;  manage(node.properties)}
    override fun manage(pattern: Pattern) {this.pattern=pattern; manage(pattern.node)}

}
