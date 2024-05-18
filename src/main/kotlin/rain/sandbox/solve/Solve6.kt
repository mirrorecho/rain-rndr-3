package rain.sandbox.solve

import rain.language.interfaces.manage
import rain.language.interfaces.manageWith
import rain.patterns.interfaces.DimensionLabel
import rain.patterns.interfaces.forEach
import rain.patterns.interfaces.manageWith
import rain.patterns.nodes.Event
import rain.rndr.nodes.Color
import rain.rndr.nodes.Value

fun main() {


    val m = Color.receives
    val e = Event.sends(m) {
        h = 200.0
        machine = Color
//        simultaneous = false
        // TODO: need a method that can create the TRIGGERS relationship

        extend(
            Event.sends(Value.receives) { value = 0.4; dur = 1.0; machine=Value },
            Event.sends(Value.receives) { value = 0.9; dur = 2.0 },
            Event.sends(Value.receives) { value = 0.0; dur = 1.0 }
        )
    }

    e.manageWith(Color.receives) {
        get(DimensionLabel.CHILDREN)?.forEach {
            it.node.properties.manageWith(Value.receives) {
                println(dur)
//                println(properties)
////                println(value)
////                println(it.cascadingProperties)
            }
        }
    }


//    e.manageWith(Event.EventManager()) {
//
//        getPatterns(DimensionLabel.CHILDREN).forEach { println(it.node) }
////        node?.let {n->
////            n[CUES_FIRST(), CUES_NEXT(), CUES_NEXT(), CUES()].forEach { println(it) }
////        }
//
////        println(pattern)
////        getPatterns(PatternDimension.CHILDREN).forEach { println(it.node) }
//    }


//    println(e.manager.pattern)

//    Cue.get().forEach { println(it) }
//
//    Cue[CUES()].forEach { println(it) }

//    e[CONTAINS(), CUES()].forEach { println(it) }

//    println(e.manager.pattern)
//    println(e.getPatterns(PatternDimension.CHILDREN))
//    val eChildren = TriggeringTree(e)[PatternDimension.CHILDREN]
//    println(eChildren.first())
//    eChildren.forEach { println(it) }

}
