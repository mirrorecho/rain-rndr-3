package rain.rndr.nodes

import org.openrndr.Program
import org.openrndr.animatable.Animatable
import org.openrndr.animatable.easing.Easing
import rain.language.NodeLabel
import rain.patterns.nodes.Machine
import rain.utils.autoKey
import kotlin.math.absoluteValue

open class ValueAnimate(
    key:String = autoKey(),
    ): ValueController(key) {
    companion object : NodeLabel<ValueAnimate>(ValueAnimate::class, Machine, { k -> ValueAnimate(k) }){
        override val receives: ReceivingManager get() = ReceivingManager()
    }
    override val label: NodeLabel<out ValueAnimate> = ValueAnimate

    class AnimationValue(
        var value: Double
    ) : Animatable()

    class ReceivingManger : Value.ReceivingManager() {

    }

    var animationValue = AnimationValue(0.0)

    override var controlValue:Double? get() = animationValue.value
        set(v) { v?.let { animationValue.value = it } }

    override fun trigger(properties: MutableMap<String, Any?>) {

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
                initValue?.let { controlValue = it }

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
            } else controlValue = v
        }
    }

    override fun render(program: Program) {
        animationValue.updateAnimation()
        super.render(program)
    }

}