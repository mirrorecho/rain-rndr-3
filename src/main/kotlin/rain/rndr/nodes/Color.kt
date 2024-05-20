package rain.rndr.nodes

import rain.language.*
import rain.rndr.relationships.*
import rain.utils.*

import org.openrndr.color.ColorHSVa
import org.openrndr.color.ColorRGBa
import rain.patterns.nodes.*

open class Color(
    key:String = autoKey(),
): Machine(key) {
    companion object : NodeLabel<Color>(Color::class, Machine, { k -> Color(k) }){
        override val receives: ReceivingManager get() = ReceivingManager()
    }
    override val label: NodeLabel<out Color> = Color

    val h = cachedTarget(H, Value)
    val s = cachedTarget(S, Value)
    val v = cachedTarget(V, Value)
    val a = cachedTarget(A, Value)

    override val targetProperties = listOf(::h, ::s, ::v, ::a)

    class ReceivingManager : Machine.ReceivingManager() {
        var h: Double by defaultable("h", 90.0)
        var s: Double by defaultable("s", 0.8)
        var v: Double by defaultable("v", 0.8)
        var a: Double by defaultable("a", 0.8)
    }

    fun colorHSVa() = ColorHSVa(
        h.target?.value ?: 90.0,
        s.target?.value ?: 0.8,
        v.target?.value ?: 0.8,
        a.target?.value ?: 0.6,
    )

    fun colorRGBa(): ColorRGBa = colorHSVa().toRGBa()

}


