package rain._bak.language


// TODO: useful?
//open class NodeManager(
//    final override val node: LanguageNode
//): Manager() {
//
//    override fun patternFactory(previous: Pattern?): Pattern = SelfPattern(node)
//
//
//    final override var properties: MutableMap<String, Any?> = node.properties
//
////    override fun use(properties: MutableMap<String, Any?>) {this.properties = properties}
//
////    override operator fun invoke(p: MutableMap<String, Any?>): NodeManager = this.apply { setProperties(p) }
//
//}


//open class Trigger<M: Machine>(
//    machineLabel:NodeLabel<out M>,
//    val pattern: Pattern,
//    usePatternProperties: Boolean = false,
//) {
//
//    val properties = if (usePatternProperties) pattern.properties else pattern.origin.properties
//
//    var machineLabel: NodeLabel<out M> by properties.apply { this["machineLabel"] = machineLabel }
//    var machinePath: List<RelationshipLabel>? by properties
//    var dur: Double by properties
//    var gate: Gate by properties
//    var simultaneous: Boolean by properties.apply { putIfAbsent("simultaneous", false) }
//
////    val triggersMachine: Machine? get() = getAs<NodeLabel<out Machine>?>("machine") ?.let { tree[TRIGGERS()](it).firstOrNull() } ?: parent?.triggersMachine
////    val triggersMachine: Machine? = machineLabel ?.let { tree[TRIGGERS()](it).firstOrNull() } ?: parent?.triggersMachine
//
//    // TODO: used?
//    operator fun invoke() {
//
//    }
//
//    // TODO: used?
//    fun update() {
//    }
//
//    open fun <T:Any?> KMutableProperty<T>.stream(vararg values:T) {
//        pattern.stream(this.name, pattern.origin.label, *values)
//    }
//
//}

