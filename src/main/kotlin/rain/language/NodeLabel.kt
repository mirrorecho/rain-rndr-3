package rain.language

import rain.language.interfaces.Context
import rain.language.interfaces.NodeLabelInterface
import rain.language.interfaces.NodeSelectable
import kotlin.reflect.KClass

open class NodeLabel<T:Node>(
    myClass: KClass<T>,
    parentLabel:NodeLabel<*>? = null,
    override val factory: (String)->T,
    ): NodeSelectable, NodeLabelInterface<T> {

    private fun getName(cl:KClass<T>) = cl.simpleName ?: "Node"

    override val isRoot: Boolean = parentLabel==null

    final override val labelName:String = getName(myClass)

    // TODO: needed?
//    override val ancestorLabels: List<NodeLabel<*>> = parentLabel?.let { listOf(it) + it.ancestorLabels }.orEmpty()

    override val allNames: List<String> = listOf(getName(myClass)) + parentLabel?.allNames.orEmpty()

    override val selectMe:SelectNodes = SelectNodes(labelName=getName(myClass))

    final override var context: Context = LocalContext // TODO: ok to just default to LocalContext here?

    override val receives: Manager get() = Manager()

    override val registry: MutableMap<String, T> = mutableMapOf()

    override fun toString() = labelName

    private fun registerMe() {
        context.nodeLabels[labelName] = this
    }

//    open fun <TT:Node>patternManager(properties: MutableMap<String, Any?>, pattern: Pattern) = PatternManager<T, TT>(properties, pattern)

    init {
        registerMe()
    }

}
