package rain.rndr.nodes

import rain.language.*
import rain.language.interfaces.*
import rain.rndr.relationships.*
import rain.utils.*

import rain.patterns.nodes.Machine

import org.openrndr.Program


open class Circle(
    key:String = autoKey(),
    ): Machine(key) {
    companion object : NodeLabel<Circle>(Circle::class, Machine, { k -> Circle(k) }) {
        override val receives: ReceivingManger get() = ReceivingManger()
    }
    override val label: NodeLabel<out Circle> = Circle

    var radius = cachedTarget(RADIUS, Value)
    var strokeWeight = cachedTarget(STROKE_WEIGHT, Value)
    var strokeColor = cachedTarget(STROKE_COLOR, Color)
    val fillColor = cachedTarget(FILL_COLOR, Color)
    val position = cachedTarget(POSITION, Position)
//
    override val targetProperties = listOf(::radius, ::strokeWeight, ::strokeColor, ::fillColor, ::position)

    class ReceivingManger : ReceivingManager() {
        var radius: Double? by properties
    }


//    // TODO: implement if needed (or remove)
    override fun trigger(properties: MutableMap<String, Any?>) {
        properties.manageWith(receives) {
            triggerValue(this@Circle.radius, radius)
        }

    }

    override fun render(program: Program) {
//        println("circle with x position " + position.x.value.toString())
        program.apply {
            drawer.fill = fillColor.target?.colorRGBa()
            drawer.stroke = strokeColor.target?.colorRGBa()
            strokeWeight.target?.apply { drawer.strokeWeight = value }
            drawer.circle(
                position.target!!.vector(program), // NOTE: ERROR IF NO POSITION
                radius.target?.value ?: 90.0,
            )
        }
    }
}
