package rain.rndr.nodes

import rain.language.*
import rain.rndr.relationships.*
import rain.utils.*

import org.openrndr.color.ColorHSVa
import org.openrndr.color.ColorRGBa
import rain.patterns.nodes.*

open class Color(
    key:String = autoKey(),
): RndrMachine(key) {
    companion object : NodeLabel<Color>(Color::class, RndrMachine, { k -> Color(k) })
    override val label: NodeLabel<out Color> = Color

    val h = cachedTarget(H, Value)
    val s = cachedTarget(S, Value)
    val v = cachedTarget(V, Value)
    val a = cachedTarget(A, Value)

    override val targetProperties = listOf(::h, ::s, ::v, ::a)

    fun colorHSVa() = ColorHSVa(
        h.target?.value ?: 90.0,
        s.target?.value ?: 0.8,
        v.target?.value ?: 0.8,
        a.target?.value ?: 0.6,
    )

    fun colorRGBa(): ColorRGBa = colorHSVa().toRGBa()

}


var Trigger<Color>.h: Double get() = properties["h"] as Double
    set(value) {properties["h"]=value}
var Trigger<Color>.s: Double get() = properties["s"] as Double
    set(value) {properties["s"]=value}
var Trigger<Color>.v: Double get() = properties["v"] as Double
    set(value) {properties["v"]=value}
var Trigger<Color>.a: Double get() = properties["a"] as Double
    set(value) {properties["a"]=value}

//fun NodeLabel<Color>.triggering(properties: Map<String, Any?>): object: Trigger(properties) {
//
//}

//inner class ColorTrigger(val properties: Map<String, Any?>) {
//
//}
