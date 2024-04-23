package rain.machines.nodes

import org.openrndr.Program
import rain.language.*
import rain.patterns.nodes.Event
import rain.patterns.nodes.TreeLineage


// TODO maybe: inherit from a general Pattern?
// TODO maybe: is this interface even worth it?
interface MachinePattern {

//    fun reset() { throw NotImplementedError() }

    // NOTE: trigger is key here... it's what fundamentally makes a machine a machine
    // ... i.e. a machine is something that's "trigger-able"
    // TODO maybe pass a simple properties map instead of TreeLineage<Event>?
    fun trigger(properties: Map<String, Any?>)

    fun render(program: Program) {
        throw(NotImplementedError("render not implemented"))
    }

}

open class Machine(
    key:String = rain.utils.autoKey(),
): MachinePattern, Node(key) {
    companion object : NodeLabel<Machine>(Machine::class, Node, { k -> Machine(k)})
    override val label: NodeLabel<out Machine> = Machine

    // TODO: is this even used?
    var isRunning = false

    override fun trigger(properties: Map<String, Any?>) {
        // TODO: implement?
        println("TRIGGERING $key: $properties - WARNING: no machine trigger defined")
        println("--------------------------------")
    }

}

open class Printer(
    key:String = rain.utils.autoKey(),
): MachinePattern, Machine(key) {
    companion object : NodeLabel<Printer>(Printer::class, Node, { k -> Printer(k) })
    override val label: NodeLabel<out Printer> = Printer

    override fun trigger(properties: Map<String, Any?>) {
        // TODO: implement
        println("PRINTER $key: $properties")
        println("--------------------------------")
    }

}