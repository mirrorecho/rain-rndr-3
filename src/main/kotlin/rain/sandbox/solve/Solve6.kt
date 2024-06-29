package rain.sandbox.solve


import rain.language.CachedTarget
import rain.language.Thingy
import rain.language.manageWith
import rain.patterns.DimensionLabel
import rain.patterns.nodes.Gate
import rain.patterns.nodes.event
import rain.patterns.nodes.*
import rain.patterns.relationships.TRIGGERS
import rain.rndr.nodes.Circle
import rain.rndr.nodes.Value
import rain.rndr.relationships.RADIUS





fun solve1() {
//
//    // TODO: remove "sends" method
//

    val crm = Circle.ReceivingManager2()

    val e1 = event() {

    }

    val e = event("E1", Circle.receives) {
        addTrigger("C1")
        gate = Gate.ON_OFF
        simultaneous = true
        radius.value = 0.0


//        targeting(radius) {
//
//        }
//
//        targeting(fillColor.h) {
//
//        }

        fun <T:Machine>event2(key:String, targeting:CachedTarget<T>): T {

        }

        val m = event2("E1", CachedTarget<Circle>())
        m.manager.extend()

        extend(

            event("E1-1", Value.receives) {

                deferToPattern {
                    it.historyDimension?.pattern
                }
//                gate = Gate.NONE // NOTE: shouldn't have to specify this
//                simultaneous = false // NOTE: shouldn't have to specify this
//                deferToPattern { p-> ValueAnimate.create().relate(ANIMATES, p.node) }
//                machinePath = arrayOf(RADIUS, ANIMATES.left)
                // TODO: are cascading properties + defaults working correctly?
                machinePath = arrayOf(RADIUS)
//                ::initValue.stream(0.0)
                ::dur.stream(2.0, 0.5, 4.0)
                ::value.stream(90.0, 200.0, 9.0)
            }
        )
    }
//    println(Circle.get("C1"))

    e.manageWith(cr) {
//
//            play()
////        val child = this[DimensionLabel.CHILDREN]?.invoke()?.first()
////        val grandChildren = child?.get(DimensionLabel.CHILDREN)
////        grandChildren?.forEach { println(it.cascadingProperties) }
//
////        val child = this[DimensionLabel.CHILDREN]?.invoke()?.first()
////        println(child?.node)
////        println(child?.history?.invoke()?.first()?.node)
////        print(child?.history?.invoke()?.toList())
////        val machineDimension = child?.get(DimensionLabel.TRIGGERS) as RelatesHistoryDimension?
////        val machine = machineDimension?.invoke()?.first()
////        println(machineDimension?.extendedRelationships?.toList())
////        println(child?.node)
////        println(machine?.node)
//
////        play()
//    }
        this[DimensionLabel.CHILDREN]?.asPatterns()?.forEach {
//            println(it.historyDimension)
//            println(it.historyDimension?.pattern?.node)
            println(it.node)
            it[DimensionLabel.CHILDREN].asPatterns().forEach { it2 ->
                println(it2.cascadingProperties)
            }
        }
    }



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
