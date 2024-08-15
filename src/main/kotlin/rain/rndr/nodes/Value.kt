package rain.rndr.nodes

import rain.language.field
import rain.patterns.nodes.Machine
import rain.utils.*


open class Value(
    key:String = autoKey(),
    ): Machine(key) {
    abstract class ValueLabel<T:Value>: MachineLabel<T>() {
        val value = field<Double, Value>("value")
    }

    companion object : ValueLabel<Value>() {
        override val labelName:String = "Value"
        override fun factory(key:String) = Value(key)
    }

    override val label = Value



}

