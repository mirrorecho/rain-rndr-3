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

        val radius = field("radius", Machine, RADIUS, 90.0)
        val position = nodeField("position", Position, POSITION, Position.CENTER)

        // TODO MAYBE: implement these xy, and hsva sub-fields
//        val x = field("x", 0.5, X)
//        val y = field("y", 0.5, Y)
        val strokeColor = nodeField("strokeColor", Color, STROKE_COLOR)
        val strokeWeight = field("strokeWeight", Machine, STROKE_WEIGHT, 0.9)
        val fillColor = nodeField("fillColor", Color, FILL_COLOR)
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
            drawer.fill = fillColor()?.colorRGBa()
            drawer.stroke = strokeColor()?.colorRGBa()
            drawer.strokeWeight = strokeWeight()
            drawer.circle(
                position = position().vector(program),
                radius()
            )
        }
    }

}


// =====================================================================================


//open class Circle(
//    key:String = autoKey(),
//    ): Machine(key) {
//    companion object : NodeLabel<Circle>(Circle::class, Machine, { k -> Circle(k) }) {
//
//        val radius = Field<Double, Machine>("radius", RADIUS, Machine)
//
//
////        val radius = CachedTarget(Event.create(), RADIUS, Value)
//
////        override val receives: ReceivingManager get() = ReceivingManager()
////
////        class PatternManager {
////            fun getPattern(): Pattern {
////                throw NotImplementedError()
////            }
////            var radius
////        }
////
////
////        // TODO, naming?
////        class Manager {
////            var radius = cachedTarget(RADIUS, Value, 4.0)
////            var position = cachedTarget(POSITION, Position) // TODO maybe: default factory?
////            val targetProperties = listOf(::radius, ::position)
////
////            inner class receivingManager {
////                var radius: Double by receiving(connected = this@Manager::radius)
////                var dur: Double  by receiving()
////            }
////
////            fun receivingManager() {}
////            fun connectedManager() {}
////
////        }
//
//    }
//    override val label: NodeLabel<out Circle> = Circle
//    fun typedThis():Circle { return this }
//
////    val m = Companion.Manager()
//
//    // TODO: cleaner (DRY) way to connect these target properties, with receiving manager, with triggering
//
//    fun <T:Any>fieldValue(block:()->Pattern<Node>.CachedTarget.FieldValue<T>) {
//
//    }
//
//    val radius by Circle.radius.cachedFieldValue()
//
//    val fieldValues:List<KProperty0<CachedTarget<out Node>>> = listOf()
//
////    val radius by Circle.radius.cachedFieldValue(
////        typedThis(), Machine
////    )
//
////    var radius2 = cachedTarget(RADIUS, Value)
////    var radius = cachedTarget(RADIUS, Value)
//
////    var strokeWeight = cachedTarget(STROKE_WEIGHT, Value)
////    var strokeColor = cachedTarget(STROKE_COLOR, Color)
////    val fillColor = cachedTarget(FILL_COLOR, Color)
////    val position = cachedTarget(POSITION, Position)
//////
////    val connected = Connected()
////
////    override val targetProperties = listOf(::radius, ::strokeWeight, ::strokeColor, ::fillColor, ::position)
//
////    class ReceivingManager : Machine.ReceivingManager() {
////        override var machineLabel: NodeLabel<out Machine>? by defaultable("machineLabel", Circle)
////        var radius by Circle
////
//////        var radius: Double by defaultable("radius",40.0)
//////        var x: Double by defaultable("x", 0.5)
//////        var y: Double by defaultable("y", 0.5)
////    }
//
//
////    // TODO: implement if needed (or remove)
//    override fun trigger(properties: MutableMap<String, Any?>) {
//
////        properties.manageWith(receives) {
////            triggerValue(this@Circle.radius, radius)
////            position.target?.x?.let { triggerValue(it, x) }
////            position.target?.y?.let { triggerValue(it, y) }
////        }
//
//    }
//
//    override fun render(program: Program) {
////        println("circle with x position " + position.x.value.toString())
//        program.apply {
////            println("rendering $this")
////            drawer.fill = fillColor.target?.colorRGBa()
//            drawer.fill = ColorRGBa.CYAN
//            drawer.stroke = strokeColor.target?.colorRGBa()
//            strokeWeight.target?.let { drawer.strokeWeight = it.value }
//            drawer.circle(
//                position.target!!.vector(program), // NOTE: ERROR IF NO POSITION
//                radius.target?.value ?: 90.0,
//            )
//        }
//    }
//}
