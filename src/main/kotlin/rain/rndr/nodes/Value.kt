package rain.rndr.nodes

import org.openrndr.Program
import rain.language._bak2.SelectDirection
import rain.language.interfacing.NodeLabel
import rain.patterns.nodes.Machine
import rain.patterns.relationships.TRIGGERS
import rain.utils.*

abstract class ValueController(
    key:String = autoKey(),
): Machine(key) {
    abstract var controlValue: Double? // would it be simpler to not allow nulls here?

    val targetValue = cachedTarget(TRIGGERS, Value, SelectDirection.LEFT)

    override fun render(program: Program) {
        controlValue?.let { targetValue.target?.value = it }
    }
}

open class Value(
    key:String = autoKey(),
    ): Machine(key) {
    companion object : NodeLabel<Value>(Value::class, Machine, { k -> Value(k) }){
        override val receives: ReceivingManager get() = ReceivingManager()
    }
    override val label: NodeLabel<out Value> = Value

    open class ReceivingManager : Machine.ReceivingManager() {
        var value: Double? by nullable("value")
    }

    var value:Double by this.properties.apply { putIfAbsent("value", 0.0) }

    override fun trigger(properties: MutableMap<String, Any?>) {
//        println(properties)
        properties["value"]?.let{ value = it as Double }
    }

    override fun render(program: Program) {
        // DO NOTHING // TODO avoid this?
    }

}

