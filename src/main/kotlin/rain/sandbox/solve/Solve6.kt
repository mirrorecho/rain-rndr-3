package rain.sandbox.solve

import rain.patterns.interfaces.PatternDimension
import rain.patterns.nodes.Cue
import rain.patterns.nodes.Event
import rain.patterns.nodes.TriggeringTree
import rain.patterns.relationships.*
import rain.rndr.nodes.Color
import rain.rndr.nodes.Value
import rain.rndr.relationships.H
import rain.rndr.relationships.S

fun main() {


    val e = Event.sends(Color.receives) {
        h = 200.0
//        simultaneous = false
        // TODO: need a method that can create the TRIGGERS relationship
        extend(
            Event.sends(Value.receives) { value = 0.4; dur = 1.0 },
            Event.sends(Value.receives) { value = 0.9; dur = 2.0 },
            Event.sends(Value.receives) { value = 0.0; dur = 1.0 }
        )
    }


    e.manageWith(Event.EventManager()) {

        getPatterns(PatternDimension.CHILDREN).forEach { println(it.node) }
        node?.let {n->
            n[CUES_FIRST(), CUES_NEXT(), CUES()].forEach { println(it) }
        }

//        println(pattern)
//        getPatterns(PatternDimension.CHILDREN).forEach { println(it.node) }
    }


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
