package rain.patterns.nodes

import rain.language.*
import rain.utils.autoKey

enum class Gate(val startGate: Boolean?, val endGate:Boolean?) {
    ON(true, null),
    OFF(null, false),
    ON_OFF(true, false),
    NONE(null, null),
}

// TODO: make this an interface!
open class Event protected constructor(
    key:String = autoKey(),
): Node(key) {
    companion object : NodeLabel<Event>(Event::class, Node, { k -> Event(k) })
    override val label: NodeLabel<out Event> = Event

    open class EventManager : Manager() {
        var machine: NodeLabel<out Machine>? by properties
        var machinePath: List<RelationshipLabel>? by properties
        var dur: Double? by properties
        var gate: Gate by properties.apply { putIfAbsent("simultaneous", Gate.NONE) }
        var simultaneous: Boolean by properties.apply { putIfAbsent("simultaneous", false) }

        init {
            updatePatternFactory { n, p, d -> TriggeringTree(n, p, d) }
        }
    }
//    override val manager = EventManager()

    // TODO: bring back the below?
//    val triggers = cachedTarget(TRIGGERS, machine!!)
//
//    fun makeTrigger(machine: Machine?=null, makeAutoTargets:Boolean=true): Event {
//        (machine ?: this.machine?.create {
//            if (makeAutoTargets) autoTarget()
//        } )?.let {
//            this.relate(TRIGGERS, it)
//        }
//        return this
//    }
//    fun makeTrigger(machineKey: String): Event {
//        this.relate(TRIGGERS, machineKey)
//        return this
//    }


//    override val thisTyped = this

    // TODO... no, maybe this should just point to a node on the graph right away!
//    var machine:NodeLabel<out RndrMachine>? get() = getUp("machine")
//        set(value) { this["machine"]=value }

//    var machinePath: List<RelationshipLabel>? get() = getUp("machinePath")
//        set(value) { this["machinePath"]=value }

//    var machineKey: String? get() = getUp("machineKey")
//        set(value) { this["machineKey"]=value }

//    var gate: Boolean? get() = getUp("gate")
//        set(value) { this["gate"]=value }
//
//    val sumDur: Double get() =
//        if (isLeaf) getUp("dur")
//        else {
//            children.map { sumDur }.run {
//                if (simultaneous) { max() } else { sum() }
//            }
//        }



    // TODO: use something like this?
//    operator fun invoke(vararg patterns: Pattern): CellTree {
//        this.extend(*patterns)
//        return this
//    }

    // TODO: implement cell building
//    fun <BT: CellBuilder> build(callback: BT.() -> Unit): Cell {
//        val cb = CellBuilder(this)
//        cb.apply(callback)
//        return this
//    }

}

// just for testing purposes
class SubEvent(
    key:String = autoKey(),
): Event(key) {
    companion object : NodeLabel<SubEvent>(SubEvent::class, Event, { k -> SubEvent(k) })
    override val label: NodeLabel<out SubEvent> = SubEvent

}

//fun yo() {
//    Event.yoMama()
//}

// TODO: review and remove when appropriate

//fun par(key:String = autoKey(), properties:Map<String, Any?>?=null, vararg children:Event):Event =
//    Event.create(key, properties) {  }

//// because it's used so often AND cascade doesn't make sense
//val TreeLineage<Event>.simultaneous get() = tree.simultaneous

// TODO: figure out if there is some way to cache this without needing to keep querying
//val TreeLineage<Event>.triggersMachine: Machine? get() = getAs<NodeLabel<out Machine>?>("machine") ?.let { tree[TRIGGERS()](it).firstOrNull() } ?: parent?.triggersMachine

//val TreeLineage<Event>.triggersPathMachine: Machine? get() = this.triggersMachine?.let {machine->
//    getAs<List<RelationshipLabel>?>("machinePath")?.let { rel->
//        getAs<NodeLabel<out Machine>?>("machinePathType")?.let {mpt ->
//            return machine.get( *(rel.map { it() }.toTypedArray()) )(mpt).first()
//        }
//    }
//    return machine
//}

//fun TreeLineage<Event>.trigger(): Machine? =
//    if (this.tree.isTrigger) this.triggersPathMachine?.also { machine ->
//        machine.trigger(properties)
//    } else null
//
//
//
//// TODO: put machinePath into properties
//fun <M: Machine>Event.Companion.triggering(key:String?=null, vararg machinePath: RelationshipLabel,  block:(PatternManager<Event, M>.()->Unit)?=null): Event =
//    Event.create(key ?: autoKey()).also { e->
//        Event.patternManager<M>(e.properties, e.getPattern()).let {
//            block?.invoke(it)
//        }
//        // TODO... any way to avoid this save?
//        e.save()
//    }

//fun <M: Machine>Event.Companion.triggering(vararg machinePath: RelationshipLabel,  block:(PatternManager<Event, M>.()->Unit)?=null): Event =
//    Event.triggering(null, *machinePath, block=block)

//var PatternManager<Event, *>.machineLabel:NodeLabel<out Machine>? get() = properties["machineLabel"] as NodeLabel<out Machine>?
//    set(value) {properties["machineLabel"]=value}
//
//// TODO: needed? (just could check the relationship instead)
//var PatternManager<Event, *>.isTrigger:Boolean? get() = properties["isTrigger"] as Boolean?
//    set(value) {properties["isTrigger"]=value}
//
//var PatternManager<Event, *>.simultaneous:Boolean? get() = properties["simultaneous"] as Boolean?
//    set(value) {properties["simultaneous"]=value}
//
//var PatternManager<Event, *>.dur:Double? get() = properties["dur"] as Double?
//    set(value) {properties["dur"]=value}
//
//var PatternManager<Event, Color>.h:Double? get() = properties["h"] as Double?
//    set(value) {properties["h"]=value}