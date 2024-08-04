package rain.language


// TODO: constructors allowing either a name, or a relationshipLabel, or both
//  ... allow some options (i.e. no relationship, optional relationship if property doesn't exist locally, etc.)

 interface Field<T:Any?> {
     val name: String
     val relationshipLabel: RelationshipLabel?
     val default: T? get() = null

     // TODO: used?
     val isLocalOnly: Boolean get() = (relationshipLabel == null)

     operator fun get(node:Node):T?

     // TODO: is this used?
     operator fun set(node:Node, value:T)

     fun updateMessageFrom(msgFrom: Message<*>, msgTo: Message<*>) {
         msgFrom[this]?.let { msgTo[this] = it }
     }

 }

open class ValueField<T:Any?>(
    override val name: String,
    override val relationshipLabel: RelationshipLabel? = null,
    val defaultToSelf:Boolean = true,
): Field<T> {

    // TODO maybe: allow the relationship to specify which attribute on the ValueNode to use
    //  (would complicate this a bit, so for now, KISS)
    fun getValueNode(node:Node): Node? {

        relationshipLabel?.let {rl ->
            node[rl()].first?.let { return it }
            if (!defaultToSelf) return null
        }
        return node

    }

    // IMPORTANT... consumer of ValueField is responsible for first calling getValueNode(),
    // caching the resultant node as applicable, and then calling get or set on that resultant node

    override operator fun get(node:Node):T? = node.properties[name] as T?

    // TODO: is this used?
    override operator fun set(node:Node, value:T) {
        // TODO: create relationship?
        // TODO maybe: could create dupe relationships if not careful... create a relateOnly to avoid?
        node.properties[name] = value
    }

}


class DefaultingValueField<T:Any>(
    name: String,
    override val default: T,
    relationshipLabel: RelationshipLabel? = null,
    defaultToSelf:Boolean = true,
): ValueField<T>(name, relationshipLabel, defaultToSelf)  {

    override operator fun get(node:Node):T = node.properties[name] as T? ?: default

}


open class NodeField<T:Node>(
    override val name: String,
    override val relationshipLabel: RelationshipLabel,
    val targetLabel:NodeLabel<T>
): Field<T> {

    override operator fun get(node:Node):T? = node[relationshipLabel()].first(targetLabel)

    // TODO: is this used?
    override operator fun set(node:Node, value:T) {
        // TODO: create relationship?
        // TODO maybe: could create dupe relationships if not careful... create a relateOnly to avoid?
        node.relate(relationshipLabel, value)
    }

}



class DefaultingNodeField<T:Node>(
    name: String,
    override val default: T,
    relationshipLabel: RelationshipLabel,
    targetLabel:NodeLabel<T>,
): NodeField<T>(name, relationshipLabel, targetLabel) {
    override operator fun get(node:Node):T = node[relationshipLabel()].first(targetLabel) ?: default
}

fun <T:Any>field(name:String, relationshipLabel: RelationshipLabel?=null, defaultToSelf: Boolean=true) =
    ValueField<T>(name, relationshipLabel, defaultToSelf)

fun <T:Any>field(name:String, default:T, relationshipLabel: RelationshipLabel?=null, defaultToSelf: Boolean=true) =
    DefaultingValueField(name, default, relationshipLabel, defaultToSelf)

fun <T:Node>field(name:String, relationshipLabel: RelationshipLabel, targetLabel:NodeLabel<T>) =
    NodeField(name, relationshipLabel, targetLabel)

fun <T:Node>field(name:String, default:T, relationshipLabel: RelationshipLabel, targetLabel:NodeLabel<T>) =
    DefaultingNodeField(name, default, relationshipLabel, targetLabel)