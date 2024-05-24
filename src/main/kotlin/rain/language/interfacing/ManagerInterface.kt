package rain.language.interfacing

import rain.language.Node
import rain.patterns.interfaces.Dimension
import rain.patterns.interfaces.Pattern
import rain.patterns.interfaces.DimensionLabel
import kotlin.reflect.KMutableProperty

// TODO: why an interface? Even worth it?
interface ManagerInterface {

    var properties: MutableMap<String, Any?>
//    val label: NodeLabelInterface<*> // TODO: consider whether we'll need this

    val node: Node?
    var pattern: Pattern?
    var extendDimension: DimensionLabel

    fun manage(properties: MutableMap<String, Any?>)
    fun manage(node: Node)
    fun manage(pattern: Pattern)

    operator fun get(label: DimensionLabel): Dimension? = pattern?.get(label)

    fun <T>getAs(name:String):T = properties[name] as T

    var deferredBlocks: MutableList<((Pattern)->Unit)>

    fun postCreate() {
        pattern?.let { p->
            deferredBlocks.forEach { it.invoke(p) }
            deferredBlocks.clear()
            return
        }
        throw Exception("calling postCreate on $this even though pattern is null")
    }

    fun deferToPattern(block: (Pattern)->Unit) {
        pattern?.let {
            block(it)
            return
        }
        deferredBlocks.add(block)
    }

    fun extend(vararg nodes: Node) =
        deferToPattern { p-> p[extendDimension].extend(*nodes) }

    fun <T:Any?> KMutableProperty<T>.stream(extendLabel: NodeLabel<*>, vararg values:T) =
        deferToPattern { p-> p[extendDimension].stream(this.name, extendLabel, *values) }
    fun <T:Any?> KMutableProperty<T>.stream(vararg values:T) =
        deferToPattern { p-> p[extendDimension].stream(this.name, p.node.label, *values) }

}

fun <T: ManagerInterface>MutableMap<String, Any?>.manageWith(manager:T, block: (T.()->Unit)?=null): T {
    manager.manage(this)
    block?.invoke(manager)
    return manager
}


//fun <T: ManagerInterface>Pattern.manageWith(manager:T, block: (T.()->Unit)?=null): T {
//    manager.manage(this)
//    block?.invoke(manager)
//    return manager
//}