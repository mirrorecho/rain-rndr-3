package rain.rndr.nodes

import rain.language.*
import rain.rndr.relationships.*
import rain.utils.*

import org.openrndr.Program
import org.openrndr.math.Vector2
import rain.patterns.nodes.Machine


open class Position(
    key:String = autoKey(),
): Machine(key) {
    companion object : NodeLabel<Position>(Position::class, Machine, { k -> Position(k) }){
        override val receives: ReceivingManager get() = ReceivingManager()
    }
    override val label: NodeLabel<out Position> = Position

    val x = cachedTarget(X, Value)
    val y = cachedTarget(Y, Value)

    override val targetProperties = listOf(::x, ::y)

    class ReceivingManager : Machine.ReceivingManager() {
        // TODO: use new delegates
        var x: Double? by properties
        var y: Double? by properties
    }

    fun vector(program: Program): Vector2 = Vector2(
        (x.target?.value ?: 0.5) * program.width,
        (y.target?.value ?: 0.5)  * program.height,
    )
}

