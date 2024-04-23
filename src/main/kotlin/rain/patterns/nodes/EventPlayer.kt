package rain.patterns.nodes

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.openrndr.Program
import org.openrndr.application
import org.openrndr.launch
import rain.interfaces.Gate
import rain.language.Node
import rain.language.NodeLabel
import rain.machines.nodes.*
import rain.patterns.relationships.*
import rain.rndr.nodes.Circle
import rain.rndr.nodes.Value
import rain.rndr.relationships.RADIUS
import rain.utils.autoKey
import javax.management.ObjectName
import kotlin.time.DurationUnit
import kotlin.time.toDuration


// just for fiddling around purposes...
open class EventPlayer(
    val treeLineageEvent: TreeLineage<Event>
) {
    private val runningMachines:MutableMap<String, Machine> = mutableMapOf()

    fun gateMachine(machine: Machine, gate:Boolean) {
//        println("gating $gate - $machine")
        if (gate) {
            runningMachines[machine.key] = machine
            machine.isRunning = true
        }
        else {
            runningMachines.remove(machine.key)
            machine.isRunning = false
        }
    }

    private suspend fun playEvent(tlEvent: TreeLineage<Event>, program: Program,  addDelay: Double?=null) {
        val machine = tlEvent.trigger()
        // TODO: implementation here assumes gating always triggers machine... is that what we want?
        machine?.let { m -> tlEvent.tree.getProperty<Gate?>("gate")?.startGate?.let { g -> gateMachine(m, g) } }
        addDelay?.let { delay(it.toDuration(DurationUnit.SECONDS)) }
        println(tlEvent)

        val threads: MutableList<Job> = mutableListOf()

        tlEvent.children.forEach {
            if (tlEvent.simultaneous)
                threads.add(program.launch { playEvent(it, program, it.getAs("dur")) })
            else
                playEvent(it, program, it.getAs("dur"))
        }
        threads.joinAll()

        machine?.let { m -> tlEvent.tree.getProperty<Gate?>("gate")?.endGate?.let { g -> gateMachine(m, g) } }
    }


    fun play() = application {

        treeLineageEvent.let { tlEvent ->
            program {
                launch { playEvent(tlEvent, this@program) }

                extend {

                    // TODO: consider ... machines are executed in no particular order, is that OK?
                    runningMachines.forEach { it.value.render(this@program) }
                }
            }
        }
    }
}



//
//class Score(
//    val machinePalette: Palette<RndrMachine<*>>,
////    val program: Program
//) {
//
//    // TODO... used now with new trigger model?
//    // TODO maybe: should the key be a pair of the machone node key PLUS the act name? (as opposed to just the act name?)
//    private val acts: MutableMap<String, Act<*>> = mutableMapOf()
//
//    private val timeCodes: MutableMap<Double, MutableList<Trigger<*>>> = mutableMapOf()
//
////    fun getAct(machineKey: String, actName: String? = null) {
////
////    }
//
////    fun updateOrCreateAct(machineFunc: MachineFunc, actName: String): Act{ //TODO maybe: machine better here than machineName?
////        val act = actions.getOrPut(actName) {
////            Act(
////                actName,
////                machineFunc,
////                machineFunc.properties.toMutableMap(),
////                this
////                // TODO: implement map of machine relationships to acts
////            )
////        }
////        return act
////    }
//
//    fun addTrigger(trigger: Trigger<*>, time:Double) {
//        val triggerList = timeCodes.getOrPut(time) {mutableListOf()}
//        triggerList.add(trigger)
//        trigger.act?.let { acts[trigger.actName] = it }
//    }
//
//    fun <AT:Act<*>>getAct(name: String): AT? = this.acts[name] as AT?
//
//    // TODO: test and document ... also, should this be the public method, or should it be something else?
//    //  ... could combine with play, and make this private
//    private fun createTriggers(pattern: CellPattern, runningTime:Double=0.0): Double {
//
//        // TODO: refactor and simplify this logic...? (also look at old python code)
//        var patternDur = 0.0
//        println(pattern)
//        if (pattern.isLeaf) {
//            // TODO maybe: handle fancy logic like hooks here?
//            pattern.veins.forEach {
//                val veinDur = it["dur"] as Double
//                val machine = machinePalette[it["machine"] as String]
//                if (pattern.simultaneous) {
//                    machine?.createTrigger(this, runningTime, it, )
//                    if (veinDur > patternDur) patternDur = veinDur
//                } else {
//                    machine?.createTrigger(this, runningTime+patternDur, it)
//                    patternDur += veinDur
//                }
//            }
//        } else {
//            pattern.branches.asTypedSequence<CellPattern>().forEach { branch ->
//                println("yo branch " + branch.toString())
//                if (pattern.simultaneous) {
//                    val branchEndTime = createTriggers(branch, runningTime)
//                    val branchDur = branchEndTime-runningTime
//                    if (branchDur > patternDur) patternDur = branchDur
//                } else {
//                    val branchEndTime = createTriggers(branch, runningTime+patternDur)
//                    patternDur = branchEndTime-runningTime
//                }
//            }
//        }
//        return runningTime + patternDur
//    }
//
//    fun readPattern(pattern: CellPattern) {
//        timeCodes.clear()
//        acts.clear()
//        createTriggers(pattern)
//    }
//
//    fun play() = application {
//
//        var prevTriggerTime = 0.0
////        println(timeCodes.toSortedMap())
//
//        timeCodes.forEach{
//            println(it.key.toString() + ":")
//            it.value.forEach {
//                println("   " + it.actName + ": " + it.properties.toString())
//            }
//        }
//
//        program {
//
//            launch {
//                timeCodes.keys.sorted().forEach { triggerTime ->
//                    val triggerList = timeCodes[triggerTime]!!
//                    val delayTime = triggerTime - prevTriggerTime
//                    if (delayTime > 0) delay((delayTime).toDuration(DurationUnit.SECONDS))
//                    launch {
//                        triggerList.forEach { tr: Trigger<*> ->
//
//                            tr.trigger()
//
//                            // NOTE: moved below  to the point of tigger creation...
//
////                            tr.rndrMachine.actFactory?.invoke(tr)?.let { act ->
////                                addAct(act)
////                                act.isRunning = true
////                                println("starting: " + act.toString())
////                            }
//
////                            if (action is Act) {
////                                action.start()
////                                // println("starting: " + action.toString())
////                            }
////                            // TODO: implement Update actions here (inc. stopping at appropriate dur)
//////                            else {
//////                                if (p["gate"] != true) launch {
//////                                    // TODO: consider accommodating ops with indeterminate durs...
//////                                    delay((p["dur"] as Double).toDuration(DurationUnit.SECONDS))
//////                                    machine.stopOp(op)
//////                                }
//////                            }
//                        }
//                    }
//                    prevTriggerTime = triggerTime
//                }
//            }
//
//            println("ACT SIZE: " + acts.size.toString())
//
//            extend {
//
//                // TODO: need to worry about acts ordering?
//                acts.forEach {
//                    val act = it.value
//                    // TODO: note that updateAnimation has to be called even if animations not running yet...
//                    //  ... due to some quirk with order of operations and triggering... consider refactoring
//                    //  ... alongside trigger launching above
//                    act.updateAnimation()
//                    if (act.isRunning) {
//                        act.render(this@Score, this)
//                    }
//                }
//
////                acts.filter { it.value.isRunning }.forEach {
////                    val act = it.value
////                    act.updateAnimation()
////                    act.render(this@Score, this)
////                }
//
//                // DO NOTHING?
//
////                animation.updateAnimation()
////                if (!animation.hasAnimations()) {
////                    animation.apply {
////                        ::x.animate(width.toDouble(), 1000, Easing.CubicInOut)
////                        ::x.complete()
////                        ::x.animate(0.0, 1000, Easing.CubicInOut)
////                        ::x.complete()
////                    }
////                }
////                drawer.fill = myColor
////                drawer.stroke = null
////                drawer.circle(animation.x, height / 2.0, 100.0)
//            }
//
//        }
//    }
//
//}