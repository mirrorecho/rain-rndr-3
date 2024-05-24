package rain.sandbox.solve

import rain.language.interfacing.manageWith
import rain.patterns.nodes.*
import rain.rndr.nodes.Circle
import rain.rndr.nodes.Value
import rain.rndr.relationships.RADIUS


fun solve1() {

    // TODO: remove "sends" method

    val cr = Circle.receives
    val e = event("E1", cr) {
        gate = Gate.ON_OFF
        simultaneous = true
        addTrigger("C1")
        extend(
            event("E1-1", Value.receives) {
//                gate = Gate.NONE // TODO: shouldn't have to specify this
//                simultaneous = false // TODO: shouldn't have to specify this
//                deferToPattern { p-> ValueAnimate.create().relate(ANIMATES, p.node) }
//                machinePath = arrayOf(RADIUS, ANIMATES.left)
                // TODO: cascading properties + defaults not working correctly !!!!!!!!!
                machinePath = arrayOf(RADIUS)
//                ::initValue.stream(0.0)
                ::dur.stream(2.0, 0.5, 4.0)
                ::value.stream(90.0, 200.0, 9.0)

            }
        )

    }

    e.manageWith(cr) {

            play()
//        val child = this[DimensionLabel.CHILDREN]?.invoke()?.first()
//        val grandChildren = child?.get(DimensionLabel.CHILDREN)
//        grandChildren?.forEach { println(it.cascadingProperties) }

//        val child = this[DimensionLabel.CHILDREN]?.invoke()?.first()
//        println(child?.node)
//        println(child?.history?.invoke()?.first()?.node)
//        print(child?.history?.invoke()?.toList())
//        val machineDimension = child?.get(DimensionLabel.TRIGGERS) as RelatesHistoryDimension?
//        val machine = machineDimension?.invoke()?.first()
//        println(machineDimension?.extendedRelationships?.toList())
//        println(child?.node)
//        println(machine?.node)

//        play()
    }
//        this[DimensionLabel.CHILDREN]?.forEach {
////            println(it.historyPattern)
////            println(it)
//        }
//    }



//    Event.get("C1").manageWith(Circle.receives) {
//        get(DimensionLabel.TRIGGERS)?.forEach {
//            println(it.node)
//        }
//    }


}

fun main() {
    solve1()
//    val m = Color.receives
//    val e = Event.sends(m) {
//        h = 200.0
//        machineLabel = Color
//        addTrigger()
////        simultaneous = false
//        // TODO: need a method that can create the TRIGGERS relationship
//
//        extend(
//            Event.sends(Value.receives) { value = 0.4; dur = 1.0; machineLabel=Value },
//            Event.sends(Value.receives) { value = 0.9; dur = 2.0 },
//            Event.sends(Value.receives) { value = 0.0; dur = 1.0 }
//        )
//    }

//    e.manageWith(Color.receives) {
//        get(DimensionLabel.CHILDREN)?.forEach {
//            it.node.properties.manageWith(Value.receives) {
//                println(dur)
////                println(properties)
//////                println(value)
//////                println(it.cascadingProperties)
//            }
//        }
//    }


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
