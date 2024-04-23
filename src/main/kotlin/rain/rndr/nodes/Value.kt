package rain.rndr.nodes

import org.openrndr.Program
import org.openrndr.animatable.Animatable
import org.openrndr.animatable.easing.Easing
import rain.language.*
import rain.utils.*
import kotlin.math.absoluteValue

class AnimationValue(
    var value: Double
):Animatable()

open class Value(
    key:String = autoKey(),
    ): RndrMachine(key) {
    companion object : NodeLabel<Value>(Value::class, RndrMachine, { k -> Value(k) })
    override val label: NodeLabel<out Value> = Value

    var animationValue = AnimationValue(0.0)

    var value get() = animationValue.value
        set(v) {animationValue.value=v}

    override fun trigger(properties: Map<String, Any?>) {

        val triggerValue = properties["value"] as Double?
        val initValue = properties["initValue"] as Double?
        val easing = properties["easing"] as String?
        val easingTyped = easing?.let { Easing.valueOf(it) } ?: Easing.None

        val dur = properties["dur"] as Double?
        val durMs: Long = ((dur ?: 0.0) * 1000).toLong()
        val animateDur = properties["animateDur"] as Double?
        val animateDurMs: Long = ((animateDur ?: 0.0) * 1000).toLong().let {
            if (it == (0).toLong() || it.absoluteValue > durMs) durMs else it
        }

        // NEED TO CALL THIS IN ORDER FOR ANIMATION TO WORK CORRECTLY IF NOT GATED
        if (!isRunning) animationValue.updateAnimation()

        triggerValue?.let { v->
            if (animateDur!=null) { // TODO: is this the best way to test for animation?
//                println("ANIM event ${event.key}: ---------------------- ")
//                println("value: ${event.value}")
//                println("animateDur: ${event.animateDur}")
//                println("easing: ${event.easing}")
//                println("initValue: ${event.initValue}")
                initValue?.let { value = it }

                animationValue.apply {
                    if (animateDurMs >= 0) {
                        ::value.animate(v, animateDurMs, easingTyped)
                        ::value.complete()
                    } else {
                        // TODO, a better way to keep current value for the duration instead of "animating" it?
                        ::value.animate(value, durMs + animateDurMs)
                        ::value.complete()
                        ::value.animate(v, animateDurMs.absoluteValue, easingTyped)
                        ::value.complete()
                    }
                }
            } else value = v
        }
    }

    override fun render(program: Program) {
        animationValue.updateAnimation()
    }

    // TODO: bring this back!!!
//    // TODO: OK that this isn't an override (due to more specific event type?)
//    fun trigger(event: ValueEvent) {
//        super.trigger(event)
//
//        value?.let {v ->
//            if (event.isAnimation) {
//                println("ANIM event ${event.key}: ---------------------- ")
//                println("value: ${event.value}")
//                println("animateDur: ${event.animateDur}")
//                println("easing: ${event.easing}")
//                println("initValue: ${event.initValue}")
//                event.initValue?.let { value = it }
//
//                animationValue.apply {
//                    if (event.animateDurMs >= 0) {
//                        ::value.animate(v, event.animateDurMs, event.easingTyped)
//                        ::value.complete()
//                    } else {
//                        // TODO, a better way to keep current value for the duration instead of "animating" it?
//                        ::value.animate(value, event.durMs + event.animateDurMs)
//                        ::value.complete()
//                        ::value.animate(v, event.animateDurMs.absoluteValue, event.easingTyped)
//                        ::value.complete()
//                    }
//                }
//            } else {
//                value = v
//                println("STATIC VALUE ${event.key}: ${event.value}")
//            }
//        }
//
//
//    }

}



//fun createValues(single:Boolean=true, vararg keys: String) {
//    keys.forEach { k ->
//        createRndrMachine(k, single) { Value(it) }
//    }
//}