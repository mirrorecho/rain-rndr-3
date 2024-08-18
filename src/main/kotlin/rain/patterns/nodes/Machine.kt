package rain.patterns.nodes

import org.openrndr.Program
import rain.language.Node
import rain.language.NodeLabel
import rain.language.fields.field
import rain.patterns.Pattern


open class Machine protected constructor(
    key:String = rain.utils.autoKey(),
): Node(key) {
    abstract class MachineLabel<T:Machine>: NodeLabel<T>() {

    }

    companion object : MachineLabel<Machine>() {
        override val labelName:String = "Machine"
        override fun factory(key:String): Machine = Machine(key)
    }

    override val label: NodeLabel<out Machine> = Machine

    open fun gate(onOff: Boolean) {
        isRunning = onOff;
    }

    open fun render(program: Program) { println("render not implemented for $this") }

    // TODO: is this even used?
    protected var isRunning = false

    // TODO: make this uni
    open fun bump(pattern:Pattern<Event>) {
        // TODO: implement?
        println("Bumping $key: $properties - WARNING: no bump defined")
    }

}

// =======================================================================

// for testing purposes..

open class Printer(
    key:String = rain.utils.autoKey(),
): Machine(key) {
    abstract class PrinterLabel<T: Printer>(): MachineLabel<T>() {
        val msg = field("msg", "NO MESSAGE DEFINED")
    }

    companion object : PrinterLabel<Printer>() {
        override val parent = Machine
        override val labelName:String = "Printer"
        override fun factory(key:String): Printer = Printer(key)
    }

    override val label: NodeLabel<out Printer> = Printer

    val msg = attachField(Printer.msg)

    override fun bump(pattern:Pattern<Event>) {
        updateAllFieldsFrom(pattern.source)
    }
}

