package rain.rndr.nodes

import rain.rndr.relationships.*
import rain.utils.*

import org.openrndr.Program
import org.openrndr.math.Vector2
import rain.language.Field
import rain.language.Message
import rain.patterns.nodes.Machine


open class Position protected constructor(
    key:String = autoKey(),
): Machine(key) {

    abstract class PositionLabel<T:Position>: MachineLabel<T>() {
        val x = Field("x", X, 0.5)
        val y = Field("y", Y, 0.5)

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

