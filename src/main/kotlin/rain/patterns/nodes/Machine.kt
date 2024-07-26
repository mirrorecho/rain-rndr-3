package rain.patterns.nodes

import rain.language.*
import rain.language.Node
import rain.language.NodeLabel
import rain.patterns.Field
import rain.patterns.Pattern
import rain.rndr.nodes.Value
import rain.rndr.relationships.Y
import kotlin.reflect.KClass



open class Machine protected constructor(
    key:String = rain.utils.autoKey(),
): Node(key) {
    // TODO: fix simplify based on new code structure with separate class def
    abstract class MachineLabel<T:Machine>(): NodeLabel<T>() {

        val dur = Field<Double, Machine>("dur", null, Machine)
        val gate = Field<Gate, Machine>("gate", null, Machine)

        // TODO/NOTE: simultaneous is really a property of the sending Event itself,
        //  to be interpreted by EventPlayer... keeping it here for now,
        //  but may make more sense to make EventPlayer a node (rename to score?), and move this there
        val simultaneous = Field<Double, Machine>("dur", null, Machine)
    }

    companion object : MachineLabel<Machine>() {
        override val labelName:String = "Machine"
        override val factory: (String) -> Machine  = { k -> Machine(k) }
    }

    override val label: NodeLabel<out Machine> = Machine

//    open class ReceivingManager : Event.EventManager()
//    open val receivingManager by lazy { ReceivingManager() }

    override fun gate(onOff: Boolean) {
        isRunning = onOff;
    }


    // TODO: is this even used?
    protected var isRunning = false

    // TODO: assume this won't be used... but review, then delete
//    override fun bump(vararg fromPatterns: Pattern) {
//        fromPatterns.forEach {
//            val machinePath: Array<RelationshipLabel>? = it.cascadingProperties.remove("machinePath") as Array<RelationshipLabel>?
//            println("triggering $this, machinePath=${machinePath?.map {mp-> mp.labelName }}, with ${it.cascadingProperties}")
//            trigger(it.cascadingProperties)
//        }
//    }

    // TODO: triggerValue still used with new messaging scheme?
    // ... TODO?: maybe this should ACTUALLY trigger the underlying value machine?
    // ... TODO?: move back to a base class for all RndrMachines?????
//    fun triggerValue(cTarget: CachedTarget<Value>, value:Double?) {
//        cTarget.target?.apply { value?.let { this.value = it }   }
//    }

    // TODO: replace with bump?
    open fun trigger(properties: MutableMap<String, Any?>) {
        // TODO: implement?
        println("TRIGGERING $key: $properties - WARNING: no machine trigger defined")
        println("--------------------------------")
    }

}

open class PrinterLabel(): MachineLabel() {
    override val labelName:String = "Printer"
    override val factory: (String) -> Machine  =  {k -> Printer(k) }
}

open class Printer(
    key:String = rain.utils.autoKey(),
): Machine(key) {
    companion object : PrinterLabel()
    override val label: PrinterLabel = Printer

    override fun trigger(properties: MutableMap<String, Any?>) {
        receives.apply {
            this.properties = properties
            println("PRINTER $key: $message")
            println("--------------------------------")
        }
    }
}
