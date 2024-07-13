package rain.patterns

import rain.language.*
import rain.patterns.nodes.*
import rain.rndr.nodes.Circle
import rain.rndr.nodes.Value
import rain.rndr.relationships.RADIUS
import rain.utils.autoKey



open class MachineMessage<ST: Event, RT:Machine>(
    properties: MutableMap<String, Any?> = mutableMapOf()
): Message<ST, RT>(properties) {
}

// TODO: is ST needed here, or can it simply be a type param on the sends method?
open class CircleMessage<ST: Event>(
    properties: MutableMap<String, Any?> = mutableMapOf()
): MachineMessage<ST, Circle>(properties) {

    // IMPORTANT: this is the crucial link... this defines what kind of machine gets created
    //  ... as well as all the possible fields
    val receiver = Circle2

    var dur:Double by properties


    fun sends(
        label:NodeLabel<ST>,
        block:CircleMessage<ST>.()->Unit,
    ): ST {

        return label.create("YO", this.properties)
    }

    // TODO: can this replace bump() on the machine node itself?
    fun receives(block:CircleMessage<ST>.()->Unit) {

    }

}

class DefinedRelationship<ST:Node, DT:Node>(
    val sourceLabel:NodeLabel<ST>,
    val relationshipLabel: RelationshipLabel,
    val destinationLabel:NodeLabel<DT>,
) {
    fun relatedTarget(source:ST): Pattern<*, ST, DT>.CachedTarget =
        RelatesPattern(source, destinationLabel, relationshipLabel = relationshipLabel).cachedTarget

    fun relatedSource(destination:DT): Pattern<*, DT, ST>.CachedTarget =
        RelatesPattern(destination, sourceLabel, relationshipLabel = relationshipLabel.left).cachedTarget

}

// TODO: constructors allowing either a name, or a relationshipLabel, or both
//  ... allow some options (i.e. no relationship, optional relationship if property doesn't exist locally, etc.)
class Field<T:Any>(
    val relationshipLabel: RelationshipLabel? = null,
    name: String,
) {
    fun <PT:Node, ST:PT, DT:PT>cachedFieldValue(
        source:ST,
        destinationLabel: NodeLabel<DT>
    ): Pattern<PT, ST, DT>.CachedTarget.FieldValue<T> {
        val pattern = RelatesPattern(source, destinationLabel, relationshipLabel = relationshipLabel)
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


abstract class Machine2(key:String):Machine(key) {

    abstract fun bump(properties: MutableMap<String, Any?>)

//    fun <DT:Machine>relatedTarget(label:NodeLabel<DT>, relationshipLabel: RelationshipLabel): Pattern<Machine, Machine, DT>.CachedTarget {
//        return RelatesPattern(this, label, relationshipLabel = relationshipLabel).cachedTarget
//    }
//
    fun <T:Any>field(field:Field<T>): Pattern<Machine, Machine, Machine>.CachedTarget.FieldValue<T> {
        val cn = field.cachedFieldValue(this, Machine)
        return cn
    }

}


// machine subclasses determine fields, and any logic for bump() and render()
// fields are defined by a type a relationship label
open class Circle2 protected constructor(
    key:String = autoKey(),
): Machine2(key) {
    companion object : NodeLabel<Circle2>(Circle2::class, Machine, { k -> Circle2(k) }) {

        class Message<ST: Node, RT:Circle2>(
            val properties: MutableMap<String, Any?> = mutableMapOf()
        ) {

        }

        //        val radius = DefinedRelationship(Circle2, RADIUS, Value)
        val radius = Field<Double>(RADIUS)
    }

    fun render() {

    }

    override val label: NodeLabel<Circle2> = Circle2

//    val radius = RelatesPattern(this, Value, relationshipLabel = RADIUS).cachedTarget
//    val radius = relatedTarget(Circle2.radius)
//    val radius by Circle2.radius.relatedTarget(this).FieldValue<Double>("radius")
    val radius by field(Circle2.radius)


    override fun bump(properties: MutableMap<String, Any?>) {
        val cm = CircleMessage<Event>(properties).receives {
            println(dur / 2.0)

            radius.apply {
                val t = this.sourcePattern.source
            }

        }
    }

}



fun yo() {

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




