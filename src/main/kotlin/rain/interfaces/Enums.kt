package rain.interfaces

// TODO: is this the right place for Enums?

enum class SelectDirection(val shortForm: String) {
    RIGHT("->"),
    LEFT("<-"),
    CONCAT("<-"),
    NONE("")
//    RIGHT_NODE("->()"),
//    LEFT_NODE("<-()"),
}

enum class Gate(val startGate: Boolean?, val endGate:Boolean?) {
    ON(true, null),
    OFF(null, false),
    ON_OFF(true, false);
}

// TODO maybe: include an enum for itemType? (NODE vs RELATIONSHIP)
