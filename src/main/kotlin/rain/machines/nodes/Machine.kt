package rain.machines.nodes

import rain.interfaces.Gate
import rain.interfaces.LanguageNode
import rain.language.*
import rain.patterns.nodes.*




open class Machine(
    key:String = rain.utils.autoKey(),
): Node(key) {
    companion object : NodeLabel<Machine>(Machine::class, Node, { k -> Machine(k)})
    override val label: NodeLabel<out Machine> = Machine

//    open class MachineManager : Manager() {
//
//        init {
//            setPatternFactory { n, p, d -> MachinePattern(n, p, d) }
//        }
//    }
//    override val manager = MachineManager()

    open class ReceivingManager : Event.EventManager()
    open val receivingManager by lazy { ReceivingManager() }



    // TODO: is this even used?
    var isRunning = false


    override fun bump(vararg fromPatterns: PatternInterface) {
        fromPatterns.forEach { trigger(it.properties) }
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
    companion object : NodeLabel<Printer>(Printer::class, Node, { k -> Printer(k) })

    override val label: NodeLabel<out Printer> = Printer

    open class ReceivingManager : Machine.ReceivingManager() {
        var message: String by properties.apply { putIfAbsent("message", "No Message Defined") }
    }

    override val receivingManager by lazy { ReceivingManager() }

    override fun trigger(properties: MutableMap<String, Any?>) {
        receivingManager.apply {
            this.properties = properties
            println("PRINTER $key: $message")
            println("--------------------------------")
        }
    }
}
