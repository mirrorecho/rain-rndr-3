package rain.rndr.nodes

import rain.patterns.nodes.*
import rain.rndr.relationships.*
import rain.utils.*

import org.openrndr.Program
import rain.language.fields.field

open class Circle protected constructor(
    key:String = autoKey(),
): Machine(key) {

    abstract class CircleLabel<T:Circle>(): MachineLabel<T>() {
        val radius = field("radius", RADIUS, 90.0)
        val position = field("position", POSITION, Position.CENTER)
//        val x = field("x", POSITION, 0.5)
//        val y = field("y", POSITION, 0.5)

        val strokeColor = field("strokeColor", STROKE_COLOR, Color)
        val strokeWeight = field("strokeWeight", STROKE_WEIGHT, 0.9)
        val fillColor = field("fillColor", FILL_COLOR, Color)
//        TODO: maybe: implement these
        val h = field<Double?>("h", FILL_COLOR) // hue would be proxy for whether entire color is null or not
//        val s = field("s", 0.9, FILL_COLOR)
//        val v = field("v", 0.9, FILL_COLOR)
//        val a = field("a", 0.8, FILL_COLOR)

        val t = listOf("s", fillColor, Color.s, S, 0.9)

    }

    companion object : CircleLabel<Circle>() {
        override val parent = Machine
        override val labelName:String = "Circle"
        override fun factory(key:String): Circle = Circle(key)
    }

    override val label = Circle

    val radius by attachField(Circle.radius)
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

