package rain.language.interfaces

import rain.patterns.interfaces.Pattern
import rain.patterns.interfaces.PatternDimension
import kotlin.reflect.KMutableProperty

interface ManagerInterface {

    var properties: MutableMap<String, Any?>
//    val label: NodeLabelInterface<*> // TODO: consider whether we'll need this

    val pattern: Pattern?
    val node: LanguageNode?
    var extendDimension: PatternDimension

    var patternFactory: (LanguageNode, Pattern?, PatternDimension?)-> Pattern

    fun use(properties: MutableMap<String, Any?>)
    fun use(node: LanguageNode)
    fun use(pattern: Pattern)

    fun <T>getAs(name:String):T = properties[name] as T

    var deferredBlock: ((Pattern)->Unit)

    fun postCreate() {
        pattern?.let {
            this.deferredBlock(it)
            this.deferredBlock = {p-> }
            return
        }
        throw Exception("calling postCreate on $this even though pattern is null")
    }

    private fun deferBlock(block: (Pattern)->Unit) {
        pattern?.let {
            block(it)
            return
        }
        deferredBlock = { p-> deferredBlock(p); block(p)  }
    }

    fun extend(vararg nodes: LanguageNode) =
        deferBlock { p-> p.extend(extendDimension, *nodes) }

    fun <T:Any?> KMutableProperty<T>.stream(extendLabel: NodeLabelInterface<*>, vararg values:T) =
        deferBlock { p-> p.stream(extendDimension, this.name, extendLabel, *values) }
    fun <T:Any?> KMutableProperty<T>.stream(vararg values:T) =
        deferBlock { p-> p.stream(extendDimension, this.name, p.node.label, *values) }

    fun makePattern(historyPattern: Pattern?, historyDimension: PatternDimension?): Pattern?

    fun updatePatternFactory(factory: (LanguageNode, Pattern?, PatternDimension?)-> Pattern)

}

fun <T: ManagerInterface>MutableMap<String, Any?>.manageWith(manager:T, block: (T.()->Unit)?=null): T {
    manager.use(this)
    block?.invoke(manager)
    return manager
}