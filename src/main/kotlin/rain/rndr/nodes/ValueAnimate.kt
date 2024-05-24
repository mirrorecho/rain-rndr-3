package rain.rndr.nodes

import org.openrndr.Program
import org.openrndr.animatable.Animatable
import org.openrndr.animatable.easing.Easing
import rain.language.interfacing.NodeLabel
import rain.language.interfacing.manageWith
import rain.patterns.nodes.Machine
import rain.rndr.relationships.ANIMATES
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

    class ReceivingManager : Value.ReceivingManager() {
        var initValue: Double? by nullable("initValue")
        var easing: Easing by defaultable("easing", Easing.None)
        var animateDur: Double? by nullable("animateDur")
    }

    var animationValue = AnimationValue(0.0)

    override var controlValue:Double? get() = animationValue.value
        set(v) { v?.let { animationValue.value = it } }

    var targetValueMachine = cachedTarget(ANIMATES, Value)

    override fun trigger(properties: MutableMap<String, Any?>) {
        properties.manageWith(receives) {
            value?.let { v->
                val durMs: Long = ((dur ?: 0.0) * 1000).toLong()
                val animateDurMs: Long = ((animateDur ?: 0.0) * 1000).toLong().let {
                    if (it == (0).toLong() || it.absoluteValue > durMs) durMs else it
                }
                // NEED TO CALL THIS IN ORDER FOR ANIMATION TO WORK CORRECTLY IF NOT GATED????
                if (!isRunning) animationValue.updateAnimation()

                if (animateDur!=null) { // TODO: is this the best way to test for animation?

                    initValue?.let { controlValue = it }

                    animationValue.apply {
                        if (animateDurMs >= 0) {
                            ::value.animate(v, animateDurMs, easing)
                            ::value.complete()
                        } else {
                            // TODO, a better way to keep current value for the duration instead of "animating" it?
                            ::value.animate(value, durMs + animateDurMs)
                            ::value.complete()
                            ::value.animate(v, animateDurMs.absoluteValue, easing)
                            ::value.complete()
                        }
                    }
                } else controlValue = v
            }

        }
    }

    override fun render(program: Program) {
        animationValue.updateAnimation()
        triggerValue(targetValueMachine, controlValue)
        super.render(program)
    }

}