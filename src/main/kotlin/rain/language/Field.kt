package rain.language

import rain.patterns.Pattern
import rain.rndr.nodes.Value


// TODO: constructors allowing either a name, or a relationshipLabel, or both
//  ... allow some options (i.e. no relationship, optional relationship if property doesn't exist locally, etc.)

 interface Field<T:Any?> {
     val name: String
     val relationshipLabel: RelationshipLabel?
     val default: T? get() = null

     // TODO: used?
     val isLocalOnly: Boolean get() = (relationshipLabel == null)

//     // TODO: is this used?
//     operator fun get(node:Node):T?
//
//     // TODO: is this used?
//     operator fun set(node:Node, value:T)

 }

open class ValueField<T:Any?>(
    override val name: String,
    override val relationshipLabel: RelationshipLabel? = null,
    val defaultToSelf:Boolean = true,
): Field<T> {

    // TODO maybe: allow the relationship to specify which attribute on the ValueNode to use
    //  (would complicate this a bit, so for now, KISS)
//    fun getValueNode(node:Node): Node? {
//
//        relationshipLabel?.let {rl ->
//            node[rl()].first?.let { return it }
//            if (!defaultToSelf) return null
//        }
//        return node
//
//    }

    // IMPORTANT... consumer of ValueField is responsible for first calling getValueNode(),
    // caching the resultant node as applicable, and then calling get or set on that resultant node

//    override operator fun get(node:Node):T? = node.properties[name] as T?
//
//    // TODO: is this used?
//    override operator fun set(node:Node, value:T) {
//        // TODO: create relationship?
//        // TODO maybe: could create dupe relationships if not careful... create a relateOnly to avoid?
//        node.properties[name] = value
//    }

}


class DefaultingValueField<T:Any>(
    name: String,
    override val default: T,
    relationshipLabel: RelationshipLabel? = null,
    defaultToSelf:Boolean = true,
): ValueField<T>(name, relationshipLabel, defaultToSelf)  {

//    override operator fun get(node:Node):T = node.properties[name] as T? ?: default

}


open class NodeField<T:Node>(
    override val name: String,
    override val relationshipLabel: RelationshipLabel,
    val targetLabel:NodeLabel<T>
): Field<T> {

//    override operator fun get(node:Node):T? = node[relationshipLabel()].first(targetLabel)
//
//    // TODO: is this used?
//    override operator fun set(node:Node, value:T) {
//        // TODO: create relationship?
//        // TODO maybe: could create dupe relationships if not careful... create a relateOnly to avoid?
//        node.relate(relationshipLabel, value)
//    }

}

class DefaultingNodeField<T:Node>(
    name: String,
    override val default: T,
    relationshipLabel: RelationshipLabel,
    targetLabel:NodeLabel<T>,
): NodeField<T>(name, relationshipLabel, targetLabel) {
//    override operator fun get(node:Node):T = node[relationshipLabel()].first(targetLabel) ?: default
}

fun <T:Any>field(name:String, relationshipLabel: RelationshipLabel?=null, defaultToSelf: Boolean=true) =
    ValueField<T>(name, relationshipLabel, defaultToSelf)

fun <T:Any>field(name:String, default:T, relationshipLabel: RelationshipLabel?=null, defaultToSelf: Boolean=true) =
    DefaultingValueField(name, default, relationshipLabel, defaultToSelf)

fun <T:Node>field(name:String, relationshipLabel: RelationshipLabel, targetLabel:NodeLabel<T>) =
    NodeField(name, relationshipLabel, targetLabel)

fun <T:Node>field(name:String, default:T, relationshipLabel: RelationshipLabel, targetLabel:NodeLabel<T>) =
    DefaultingNodeField(name, default, relationshipLabel, targetLabel)

// ======================================================================

abstract class ConnectedField<T:Any?, CN:Node> {

    abstract val field: Field<T>

    abstract val pattern: Pattern<CN>

    protected var cachedNode: CN? = null

    protected abstract val defaultNode: Node?

    open val node: Node? get() = cachedNode ?: defaultNode

    fun connect() {
        cachedNode = pattern().firstOrNull()
    }

    fun connect(node:CN?) {
        pattern.clear()
        node?.let { pattern.extend(it) }
        cachedNode = node
    }

    abstract var value: T?

}


class ConnectedValueField<T:Any?, CN:Node>(
    override val field: ValueField<T>,
    override val pattern: Pattern<CN>,
): ConnectedField<T, CN>() {

    override val defaultNode = if (field.defaultToSelf) pattern.source else null

    override var value: T? get() = node?.properties?.get(this.field.name) as T? ?: this.field.default
        set(value) { node?.properties?.set(this.field.name, value) }


}

class ConnectedNodeField<CN:Node>(
    override val field: NodeField<CN>,
    override val pattern: Pattern<CN>,
): ConnectedField<CN, CN>() {

    override val defaultNode: CN? = field.default

    override val node: CN? get() = cachedNode ?: defaultNode

    override var value: CN? get() = node
        set(value) { node?.properties?.set(this.field.name, value) }

}