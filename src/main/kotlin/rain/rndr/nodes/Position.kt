package rain.rndr.nodes

import rain.interfaces.*
import rain.language.*
import rain.rndr.relationships.*
import rain.utils.*

import org.openrndr.Program
import org.openrndr.math.Vector2
import rain.patterns.nodes.Trigger


open class Position(
    key:String = autoKey(),
): RndrMachine(key) {
    companion object : NodeLabel<Position>(Position::class, RndrMachine, { k -> Position(k) })
    override val label: NodeLabel<out Position> = Position

    val x = cachedTarget(X, Value)
    val y = cachedTarget(Y, Value)

    override val targetProperties = listOf(::x, ::y)

    fun vector(program: Program): Vector2 = Vector2(
        (x.target?.value ?: 0.5) * program.width,
        (y.target?.value ?: 0.5)  * program.height,
    )
}

var Trigger<Position>.x: Double get() = properties["x"] as Double
    set(value) {properties["x"]=value}
var Trigger<Position>.y: Double get() = properties["y"] as Double
    set(value) {properties["y"]=value}