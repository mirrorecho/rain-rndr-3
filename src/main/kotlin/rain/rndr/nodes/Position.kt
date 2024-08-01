package rain.rndr.nodes

import rain.language.*
import rain.rndr.relationships.*
import rain.utils.*

import org.openrndr.Program
import org.openrndr.math.Vector2
import rain.patterns.nodes.Machine


open class Position protected constructor(
    key:String = autoKey(),
): Machine(key) {

    abstract class PositionLabel<T:Position>: MachineLabel<T>() {
        val x = field("x", 0.5, X)
        val y = field("y", 0.5, Y)

        override val fields = super.fields + getFields(x, y)
    }

    companion object : PositionLabel<Position>() {
        override val labelName:String = "Position"
        override fun factory(key:String) = Position(key)
    }

    override val label = Position
    override val message = Message(Position)


    fun vector(program: Program): Vector2 = Vector2(

        message[x]!! * program.width,
        message[y]!! * program.height,
    )
}

