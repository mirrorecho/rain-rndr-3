package rain.language

import rain.graph.Graph
import rain.graph.interfaces.*
import rain.language.interfaces.*
import rain.language.interfaces.Context

// TODO: seems like most of this logic could move to the interface
open class BaseContext<G:GraphInterface>(
    override val graph: G
): Context {

    // TODO maybe: consider tracking node labels with the context
    override val nodeLabels:MutableMap<String, NodeLabel<*>> = mutableMapOf()
//
//    fun <T:Node>getNodeLabel(name:String):NodeLabel<T> {
//        return nodeLabels[name] as NodeLabel<T>
//    }
//
//    inline fun <reified T:Node>getLabel():NodeLabel<T>? {
//        return typeOf<T>()::class.simpleName?.let { getNodeLabel(it) }
//    }

    // TODO: maybe these could move into the context of the label????
    private val fancyProperties: MutableMap<String, FancyProperty<*>> = mutableMapOf()

    override fun setFancyProperty(fancyProperty: FancyProperty<*>) {
        fancyProperties[fancyProperty.universalName] = fancyProperty
    }

    override fun <T> getFancyProperty(universalName: String): FancyProperty<T> {
        return fancyProperties[universalName] as FancyProperty<T>
    }

    override fun <T : LanguageNode>selectNodes(select: SelectInterface, label: NodeLabelInterface<T>): Sequence<T> = sequence {
        graph.selectGraphNodes(select).forEach {
            yield(label.from(it))
        }
    }

    override fun selectNodes(select: SelectInterface): Sequence<LanguageNode> = sequence {
        graph.selectGraphNodes(select).forEach {
            this@BaseContext.nodeLabels[it.labelName]?.from(it)?.let { n -> yield(n) }
        }
    }

    override fun <T : LanguageRelationship>selectRelationships(select: SelectInterface, label: RelationshipLabelInterface<T>): Sequence<T> = sequence {
        graph.selectGraphRelationships(select).forEach {
            yield(label.from(it))
        }
    }

    override fun selectNodeKeys(select: SelectInterface): Sequence<String> = sequence {
        graph.selectGraphNodes(select).forEach {
            yield(it.key)
        }
    }

    override fun selectRelationshipKeys(select: SelectInterface): Sequence<String> = sequence {
        graph.selectGraphRelationships(select).forEach {
            yield(it.key)
        }
    }


}

object LocalContext:BaseContext<GraphInterface>(graph = Graph())