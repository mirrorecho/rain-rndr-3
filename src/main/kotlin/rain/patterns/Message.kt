package rain.patterns

import rain.language.*
import rain.patterns.nodes.*
import rain.rndr.nodes.Circle
import rain.rndr.nodes.Value
import rain.rndr.relationships.RADIUS
import rain.utils.autoKey

open class Message<ST: Node, RT:Node>(
    val properties: MutableMap<String, Any?> = mutableMapOf()
) {

}

open class MachineMessage<ST: Event, RT:Machine>(
    properties: MutableMap<String, Any?> = mutableMapOf()
): Message<ST, RT>(properties) {
}

open class CircleMessage<ST: Event>(
    properties: MutableMap<String, Any?> = mutableMapOf()
): MachineMessage<ST, Circle>(properties) {

    val receiverLabel = Circle

    var dur:Double by properties


    fun sends(
        label:NodeLabel<ST>,
        block:CircleMessage<ST>.()->Unit,
    ): ST {

        return label.create("YO", this.properties)
    }

    fun receives(block:CircleMessage<ST>.()->Unit) {

    }

}

abstract class Machine2(key:String):Machine(key) {

    abstract fun bump(properties: MutableMap<String, Any?>)

    fun <DT:Machine>relatedTarget(label:NodeLabel<DT>, relationshipLabel: RelationshipLabel): Pattern<Machine, Machine, DT>.CachedTarget {
        return RelatesPattern(this, label, relationshipLabel = relationshipLabel).cachedTarget
    }

}




open class Circle2 protected constructor(
    key:String = autoKey(),
): Machine2(key) {
    companion object : NodeLabel<Circle2>(Circle2::class, Machine, { k -> Circle2(k) }) {
        fun radius(circle:Circle2) = circle.relatedTarget()
    }
    override val label: NodeLabel<Circle2> = Circle2

//    val radius = RelatesPattern(this, Value, relationshipLabel = RADIUS).cachedTarget
    val radius = relatedTarget(Value, RADIUS)

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

    val cm = CircleMessage<Event>()

    val e = cm.sends(Event) {
        dur = 5.0
    }

}