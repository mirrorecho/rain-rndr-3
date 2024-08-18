package rain.rndr.nodes

import org.openrndr.Program
import org.openrndr.animatable.Animatable
import org.openrndr.animatable.easing.Easing
import rain.language.*
import rain.language.fields.field
import rain.rndr.relationships.ANIMATES
import rain.utils.autoKey
import kotlin.math.absoluteValue




open class ValueAnimate(
    key:String = autoKey(),
): Value(key) {
    abstract class ValueAnimateLabel<T:ValueAnimate>: ValueLabel<T>() {
        val initValue = field<Double?>("value")
        val easing = field("easing", Easing.None)
        val animateDur = field<Double?>("animateDur")

        // TODO: would this be used? Or just add to the confusion (for now, KISS)
        //  ... could be used to animates a field on ANOTHER node somewhere
//        val animates = field<Double?>("animates", ANIMATES)
    }

    companion object : ValueAnimateLabel<ValueAnimate>() {
        override val parent = Value
        override val labelName:String = "ValueAnimate"
        override fun factory(key:String) = ValueAnimate(key)
    }

    override val label: NodeLabel<out ValueAnimate>  = ValueAnimate

    private class AnimationValue(
        var value:Double = 0.0
    ): Animatable()

    val initValue = attachField(ValueAnimate.initValue)
    val easing = attachField(ValueAnimate.easing)
    val animateDur = attachField(ValueAnimate.animateDur)

    private val animationValue = AnimationValue(0.0)

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