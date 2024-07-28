package rain.language

import rain.language.*
import rain.patterns.Pattern
import rain.patterns.RelatesPattern
import rain.patterns.nodes.*
import rain.rndr.relationships.POSITION

// TODO: consider making message an interface,
//  with a SenderMessage and ReceiverMessage
//  (e.g. only the ReceiverMessage would support fieldNodes)

interface Message<R:Node, RL:NodeLabel<R>> {
    val receiverLabel:RL

    operator fun <T:Any>get(field: Field<T>): T?

}


class LocalMessage<R:Node, RL:NodeLabel<R>>(
    override val receiverLabel:RL,
    val properties: MutableMap<String, Any?> = mutableMapOf()
): Message<R, RL>{


    operator fun <T:Any>get(field: Field<T>): T? =  {
        (fieldNodes[field.name]?.properties ?: this.properties).let {
            return it[field.name] as T? ?: field.default
        }
    }

    operator fun <T:Any>set(field: Field<T>, value:T) {
        (fieldNodes[field.name]?.properties ?: this.properties).let {
            it[field.name] = value
        }
    }

    // TODO maybe: check against fields for getting/setting by name?
    // (assume no, to be able to use very specific properties without creating fields all the time)
    operator fun get(propertyName:String) = this.properties[propertyName]

    operator fun set(propertyName:String, value:Any?) {
        this.properties[propertyName] = value
    }
}


class ConnectedMessage<R:Node, RL:NodeLabel<R>>(
    override val receiverLabel:RL,
): Message<R, RL>{

    // TODO!!!!!!!!!!
    //  need to implement a wireup method to populate this

    fun wireup(receiverNode:R) {
        receiverNode.label.fields.forEach { f ->
            val relatedNode: Node? = receiverNode[f.value.relationshipLabel!!()].first
            fieldNodes[f.key] = relatedNode ?: receiverNode
        }
    }

    val fieldNodes: MutableMap<String, Node> = mutableMapOf()

    operator fun <T:Any>get(field: Field<T>): T? {
        (fieldNodes[field.name]?.properties ?: this.properties).let {
            return it[field.name] as T? ?: field.default
        }
    }

    operator fun <T:Any>set(field: Field<T>, value:T) {
        (fieldNodes[field.name]?.properties ?: this.properties).let {
            it[field.name] = value
        }
    }

    // TODO maybe: check against fields for getting/setting by name?
    // (assume no, to be able to use very specific properties without creating fields all the time)
    operator fun get(propertyName:String) = this.properties[propertyName]

    operator fun set(propertyName:String, value:Any?) {
        this.properties[propertyName] = value
    }
}




// TODO: review and then remove...

//class DefinedRelationship<ST:Node, DT:Node>(
//    val sourceLabel:NodeLabel<ST>,
//    val relationshipLabel: RelationshipLabel,
//    val destinationLabel:NodeLabel<DT>,
//) {
//    fun relatedTarget(source:ST): Pattern<*, ST, DT>.CachedTarget =
//        RelatesPattern(source, destinationLabel, relationshipLabel = relationshipLabel).cachedTarget
//
//    fun relatedSource(destination:DT): Pattern<*, DT, ST>.CachedTarget =
//        RelatesPattern(destination, sourceLabel, relationshipLabel = relationshipLabel.left).cachedTarget
//
//}




//    private var senderBlock: ((ST)->Unit)? = null
//
//    // TODO: naming?
//    fun withSender(block: (ST)->Unit) {
//        senderBlock = block
//    }
//
//    // TODO: naming?
//    fun forSender(sender:ST) {
//        senderBlock?.invoke(sender)
//    }
//
//    fun bumps(key:String = autoKey()) {
//
//    }

//    fun <T:Any>getValue(field:Field<T, *>): T? {
//        (fieldNodes[field.name]?.properties ?: this.properties).let {
//            return it[field.name] as T?
//        }
//    }


    // TODO: used? (assume not)
//    fun <ST:Node>sends(
//        senderLabel:NodeLabel<ST>,
//        block:Message<RT, RL>.()->Unit,
//    ): ST {
//
//        return senderLabel.create("YO", this.properties)
//    }
//
//    // TODO: can this replace bump() on the machine node itself?
//    fun receives(block:Message<RT, RL>.()->Unit) {
//
//    }









