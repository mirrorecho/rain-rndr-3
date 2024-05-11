package rain.rndr.nodes

import org.openrndr.Program
import org.openrndr.animatable.Animatable
import org.openrndr.animatable.easing.Easing
import rain.interfaces.SelectDirection
import rain.language.*
import rain.patterns.nodes.Trigger
import rain.patterns.relationships.TRIGGERS
import rain.utils.*
import kotlin.math.absoluteValue

abstract class ValueController(
    key:String = autoKey(),
): RndrMachine(key) {
    abstract var controlValue: Double? // would it be simpler to not allow nulls here?

    val targetValue = cachedTarget(TRIGGERS, Value, SelectDirection.LEFT)

    override fun render(program: Program) {
        controlValue?.let { targetValue.target?.value = it }
    }
}

open class AnimateValue(
    key:String = autoKey(),
    ): ValueController(key) {
    companion object : NodeLabel<AnimateValue>(AnimateValue::class, RndrMachine, { k -> AnimateValue(k) })
    override val label: NodeLabel<out AnimateValue> = AnimateValue

    class AnimationValue(
        var value: Double
    ) : Animatable()

    var animationValue = AnimationValue(0.0)

    override var controlValue:Double? get() = animationValue.value
        set(v) { v?.let { animationValue.value = it } }

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

open class Value(
    key:String = autoKey(),
    ): RndrMachine(key) {
    companion object : NodeLabel<Value>(Value::class, RndrMachine, { k -> Value(k) })
    override val label: NodeLabel<out Value> = Value

    var value:Double by this.properties.apply { putIfAbsent("value", 0.0) } // TODO maybe: don't use by this.properties?


    override fun trigger(properties: Map<String, Any?>) {
        properties["value"]?.let{ value = it as Double }
    }

    override fun render(program: Program) {
        // DO NOTHING // TODO avoid this?
    }

}

var Trigger<Value>.value: Double get() = this.properties["value"] as Double
    set(value:Double) {this.properties["value"]=value}

var Trigger<AnimateValue>.value: Double get() = this.properties["value"] as Double
    set(value:Double) {this.properties["value"]=value}
var Trigger<AnimateValue>.initValue: Double get() = this.properties["initValue"] as Double
    set(value:Double) {this.properties["initValue"]=value}
var Trigger<AnimateValue>.easing: String? get() = this.properties["easing"] as String?
    set(value:String?) {this.properties["easing"]=value}
var Trigger<AnimateValue>.animateDur: Double get() = this.properties["animateDur"] as Double
    set(value:Double) {this.properties["animateDur"]=value}