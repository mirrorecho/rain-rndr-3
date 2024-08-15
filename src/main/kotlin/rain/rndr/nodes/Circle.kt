package rain.rndr.nodes

import rain.language.*
import rain.patterns.*
import rain.patterns.nodes.*
import rain.rndr.relationships.*
import rain.utils.*

import org.openrndr.Program

open class Circle protected constructor(
    key:String = autoKey(),
): Machine(key) {

    abstract class CircleLabel<T:Circle>(): MachineLabel<T>() {

        val radius = field("radius", RADIUS, 90.0)
        val position = field("position", POSITION, Position.CENTER)

        // TODO MAYBE: implement these xy, and hsva sub-fields
//        val x = field("x", 0.5, X)
//        val y = field("y", 0.5, Y)
        val strokeColor = nodeField<Color?>("strokeColor", STROKE_COLOR)
        val strokeWeight = field("strokeWeight", STROKE_WEIGHT, 0.9)
        val fillColor = nodeField<Color?>("fillColor", FILL_COLOR)
//        val h = field("h", 90.0, H)
//        val s = field("s", 0.9, S)
//        val v = field("v", 0.9, V)
//        val a = field("a", 0.8, A)

    }

    companion object : CircleLabel<Circle>() {
        override val labelName:String = "Circle"
        override fun factory(key:String) = Circle(key)
    }

    override val label = Circle

    val radius = attachField(Circle.radius)
    val position = attachField(Circle.position)
    val strokeColor = attachField(Circle.strokeColor)
    val strokeWeight = attachField(Circle.strokeWeight)
    val fillColor = attachField(Circle.fillColor)


    //    // TODO: implement if needed (or remove)
//    override fun bump(properties: MutableMap<String, Any?>) {
//
//    }

    override fun render(program: Program) {
//        println("circle with x position " + position.x().toString())
        program.apply {

//            println("rendering $this")
            drawer.fill = fillColor.value?.colorRGBa()
            drawer.stroke = strokeColor.value?.colorRGBa()
            drawer.strokeWeight = strokeWeight.value
            drawer.circle(
                position = position.value.vector(program),
                radius.value
            )
        }
    }

}

