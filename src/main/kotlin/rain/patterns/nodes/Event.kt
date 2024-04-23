package rain.patterns.nodes

import rain.language.*
import rain.machines.nodes.Machine
import rain.patterns.relationships.TRIGGERS
import rain.rndr.nodes.RndrMachine
import rain.rndr.nodes.Value
import rain.utils.autoKey

class Event(
    key:String = autoKey(),
): Tree(key) {
    companion object : NodeLabel<Event>(Event::class, Node, { k -> Event(k) })
    override val label: NodeLabel<out Event> = Event

    override val lineage: TreeLineage<Event> get() = TreeLineage(this, label)

    // TODO: needed?
//    override val lineageProperties: List<String> = listOf("machinePath", "dur", "value")

    val isTrigger:Boolean by this.properties.apply { putIfAbsent("isTrigger", true) }

    var simultaneous: Boolean by this.properties.apply { putIfAbsent("simultaneous", false) }

//    var machine:NodeLabel<out RndrMachine>? by this.properties


    fun play() {
        EventPlayer(TreeLineage(this, Event)).play()
    }

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

// because it's used so often AND cascade doesn't make sense
val TreeLineage<Event>.simultaneous get() = tree.simultaneous

// TODO: figure out if there is some way to cache this without needing to keep querying
val TreeLineage<Event>.triggersMachine: Machine? get() = getAs<NodeLabel<out Machine>?>("machine") ?.let { tree[TRIGGERS()](it).firstOrNull() } ?: parent?.triggersMachine

val TreeLineage<Event>.triggersPathMachine: Machine? get() = this.triggersMachine?.let {machine->
    getAs<List<RelationshipLabel>?>("machinePath")?.let { rel->
        getAs<NodeLabel<out Machine>?>("machinePathType")?.let {mpt ->
            return machine.get( *(rel.map { it() }.toTypedArray()) )(mpt).first()
        }
    }
    return machine
}

fun TreeLineage<Event>.trigger(): Machine? =
    if (this.tree.isTrigger) this.triggersPathMachine?.also { machine->
        machine.trigger(properties)
    } else null
