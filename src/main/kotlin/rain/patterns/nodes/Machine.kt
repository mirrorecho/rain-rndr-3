package rain.patterns.nodes

import rain.language.*
import rain.language.interfacing.NodeLabel
import rain.patterns.interfaces.Pattern
import rain.rndr.nodes.Value


open class Machine(
    key:String = rain.utils.autoKey(),
): Node(key) {
    companion object : NodeLabel<Machine>(Machine::class, Node, { k -> Machine(k) }) {
        override val receives: ReceivingManager get() = ReceivingManager()
    }
    override val label: NodeLabel<out Machine> = Machine

    open class ReceivingManager : Event.EventManager()
//    open val receivingManager by lazy { ReceivingManager() }

    override fun gate(onOff: Boolean) {
        isRunning = onOff;
    }

    // TODO: is this even used?
    protected var isRunning = false


    override fun bump(vararg fromPatterns: Pattern) {
        fromPatterns.forEach {
            val machinePath: Array<RelationshipLabel>? = it.cascadingProperties.remove("machinePath") as Array<RelationshipLabel>?
            println("triggering $this, machinePath=${machinePath?.map {mp-> mp.labelName }}, with ${it.cascadingProperties}")
            trigger(it.cascadingProperties)
        }
    }

    // TODO: maybe this should ACTUALLY trigger the underlying value machine?
    // TODO: move back to a base class for all RndrMachines?????
    fun triggerValue(cTarget: CachedTarget<Value>, value:Double?) {
        cTarget.target?.apply { value?.let { this.value = it }   }
    }

    open fun trigger(properties: MutableMap<String, Any?>) {
        // TODO: implement?
        println("TRIGGERING $key: $properties - WARNING: no machine trigger defined")
        println("--------------------------------")
    }

}

open class Printer(
    key:String = rain.utils.autoKey(),
): Machine(key) {
    companion object : NodeLabel<Printer>(Printer::class, Node, { k -> Printer(k) }){
        override val receives: ReceivingManager get() = ReceivingManager()
    }
    override val label: NodeLabel<out Printer> = Printer

    open class ReceivingManager : Machine.ReceivingManager() {
        var message: String by properties.apply { putIfAbsent("message", "No Message Defined") }
    }

//    override val receivingManager by lazy { ReceivingManager() }

    override fun trigger(properties: MutableMap<String, Any?>) {
        receives.apply {
            this.properties = properties
            println("PRINTER $key: $message")
            println("--------------------------------")
        }
    }
}
