package rain.language

import rain.interfaces.*
import rain.patterns.nodes.*
import kotlin.reflect.KMutableProperty

interface ManagerInterface {

    var properties: MutableMap<String, Any?>
//    val label: NodeLabelInterface<*> // TODO: consider whether we'll need this

    val pattern: PatternInterface?
    val node: LanguageNode?
    var extendDimension: PatternInterface.Dimension

    var patternFactory: (LanguageNode, PatternInterface?, PatternInterface.Dimension?)->PatternInterface

    fun use(properties: MutableMap<String, Any?>)
    fun use(node: LanguageNode)
    fun use(pattern: PatternInterface)

    fun <T>getAs(name:String):T = properties[name] as T

    var deferredBlock: ((PatternInterface)->Unit)

    fun postCreate() {
        pattern?.let {
            this.deferredBlock(it)
            this.deferredBlock = {p-> }
            return
        }
        throw Exception("calling postCreate on $this even though pattern is null")
    }

    private fun deferBlock(block: (PatternInterface)->Unit) {
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

    fun makePattern(historyPattern: PatternInterface?, historyDimension:PatternInterface.Dimension?): PatternInterface?

    fun setPatternFactory(factory: (LanguageNode, PatternInterface?, PatternInterface.Dimension?)->PatternInterface)

}


fun <T:ManagerInterface>MutableMap<String, Any?>.manageWith(manager:T, block: (T.()->Unit)?=null): T {
    manager.use(this)
    block?.invoke(manager)
    return manager
}

open class Manager: ManagerInterface {
    final override var properties: MutableMap<String, Any?> = mutableMapOf()

    private var myNode: LanguageNode? = null

    override var deferredBlock: ((PatternInterface)->Unit) = { p-> }

    override val node get() = myNode

    final override val pattern get() =
        myPattern ?: makePattern(null, null)

    private var myPattern: PatternInterface? = null

    override fun makePattern(historyPattern: PatternInterface?, historyDimension:PatternInterface.Dimension?) =
        node?.let { n-> patternFactory(n, historyPattern, historyDimension).also { myPattern=it } }

    override fun use(properties: MutableMap<String, Any?>) {this.properties = properties}
    override fun use(node: LanguageNode) {myNode = node;  use(node.properties)}
    override fun use(pattern: PatternInterface) {myPattern=pattern; use(pattern.node)}

    override var patternFactory: (LanguageNode, PatternInterface?, PatternInterface.Dimension?)->PatternInterface = { n,p,d-> Pattern(n,p,d) }

    override var extendDimension = PatternInterface.Dimension.CHILDREN

    final override fun setPatternFactory(factory: (LanguageNode, PatternInterface?, PatternInterface.Dimension?)->PatternInterface) {
        patternFactory = factory
    }

}

// TODO: useful?
//open class NodeManager(
//    final override val node: LanguageNode
//): Manager() {
//
//    override fun patternFactory(previous: Pattern?): Pattern = SelfPattern(node)
//
//
//    final override var properties: MutableMap<String, Any?> = node.properties
//
////    override fun use(properties: MutableMap<String, Any?>) {this.properties = properties}
//
////    override operator fun invoke(p: MutableMap<String, Any?>): NodeManager = this.apply { setProperties(p) }
//
//}


//open class Trigger<M: Machine>(
//    machineLabel:NodeLabel<out M>,
//    val pattern: Pattern,
//    usePatternProperties: Boolean = false,
//) {
//
//    val properties = if (usePatternProperties) pattern.properties else pattern.origin.properties
//
//    var machineLabel: NodeLabel<out M> by properties.apply { this["machineLabel"] = machineLabel }
//    var machinePath: List<RelationshipLabel>? by properties
//    var dur: Double by properties
//    var gate: Gate by properties
//    var simultaneous: Boolean by properties.apply { putIfAbsent("simultaneous", false) }
//
////    val triggersMachine: Machine? get() = getAs<NodeLabel<out Machine>?>("machine") ?.let { tree[TRIGGERS()](it).firstOrNull() } ?: parent?.triggersMachine
////    val triggersMachine: Machine? = machineLabel ?.let { tree[TRIGGERS()](it).firstOrNull() } ?: parent?.triggersMachine
//
//    // TODO: used?
//    operator fun invoke() {
//
//    }
//
//    // TODO: used?
//    fun update() {
//    }
//
//    open fun <T:Any?> KMutableProperty<T>.stream(vararg values:T) {
//        pattern.stream(this.name, pattern.origin.label, *values)
//    }
//
//}

