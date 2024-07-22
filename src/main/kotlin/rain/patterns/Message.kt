package rain.patterns

import rain.language.*
import rain.patterns.nodes.*
import rain.rndr.nodes.Circle
import rain.rndr.nodes.Value
import rain.rndr.relationships.RADIUS
import rain.utils.autoKey



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

// TODO: constructors allowing either a name, or a relationshipLabel, or both
//  ... allow some options (i.e. no relationship, optional relationship if property doesn't exist locally, etc.)
class Field<out T:Any, out NT:Node>(
//    val sourceLabel: NodeLabel<PT>,
    val name: String,
    val relationshipLabel: RelationshipLabel? = null,
    val destinationLabel: NodeLabel<out NT>,
) {

    fun
//            <
//            PT:Node,
//            ST:PT,
//            DT:PT
//            >
            cachedFieldValue(
//
    ): Pattern<NT>.CachedTarget.FieldValue<T> {
        val pattern = RelatesPattern(null, destinationLabel, relationshipLabel = relationshipLabel)
        val ct = pattern.cachedTarget

        // TODO: avoid querying here ... move to FieldValue
        val name = source.getRelationships(relationshipLabel).first().properties["field"] as String

        val fieldValue = ct.FieldValue<T>(name)
        return fieldValue
    }
}

class NodeField<T:Node>(
    val relationshipLabel: RelationshipLabel
) {

}



class Message<RT:Node, RL:NodeLabel<RT>>(
    val receiverLabel:RL,
    var properties: MutableMap<String, Any?> = mutableMapOf()
) {

    // TODO: how is this populated?
    val fieldNodes: MutableMap<String, Node> = mutableMapOf()

    operator fun <T:Any>get(field:Field<T, *>): T? {
        (fieldNodes[field.name]?.properties ?: this.properties).let {
            return it[field.name] as T?
        }
    }

    operator fun <T:Any>set(field:Field<T, *>, value:T) {
        (fieldNodes[field.name]?.properties ?: this.properties).let {
            it[field.name] = value
        }
    }

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

}



fun yo() {




    Event.sends(Circle) {
        it[radius] = 1.0
        it[dur] = 1.0


    }


    m.apply {
        receiverLabel.apply {
            set(radius, 1.0)
        }
        radius(this) = 1.0
        this[Circle.radius] = 1.0
        Circle.apply {
            set(radius, 1.0)
        }

        set(Circle.radius, 1.0)
        stream(Circle.radius, 1.0, 1.0, 1.0)
    }



    // IMPORTANT: messages cascade IFF
    // replace below with something like Event.sends(CircleMessage)
    CircleMessage<Event>().sends(Event) {
        connectMachine() // connects to newly created or existing machine
        gate = "ON_OFF"
        receiver.radius.container() { // IMPORTANT: container creates an event that doesn't bump... only has child events
            dur.stream(1.0, 1.0, 2.0)
            value.stream(90.0, 200.0, 20.0)
            stream { yo="mama0" } { } { yo="mama1" }
        }
    }

}




