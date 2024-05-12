package rain._bak.patterns
//import rain.interfaces.*
//import rain.language.*
//import rain.patterns.selects.CuePath


//open class MachinePattern(
//    override val node: LanguageNode,
//    override val historyPattern: PatternInterface? = null,
//    override val historyDimension: PatternInterface.Dimension? = null,
//): PatternInterface {
//    override fun receive(dimension: PatternInterface.Dimension, sender: PatternInterface) {
//        node.bump(dimension: Dimension,sender)
//    }
//}


//
//interface Pattern {
//
//    // TODO: caching?
//    val node: LanguageNode
//    val parents: Sequence<Pattern>
//    val children: Sequence<Pattern>
//    val receiver: Pattern
//
////    fun <T:Node>receiver(label: NodeLabel<out T>): T? = null // TODO: make this a sequence? (to allow for multiple triggers?)
//
//    val descendants: Sequence<Pattern> get() = sequence {
//        yield(this@Pattern)
//        yieldAll(this@Pattern.children)
//    }
//
//    fun saveAll() {
//        descendants.forEach { it.node.save() }
//    }
//
//    val properties: MutableMap<String, Any?> get() = node.properties
//
//    operator fun invoke() { println("WARNING: invoke not implemented for $this") } // used for things like sending/triggering
//    fun play() = PatternPlayer(this).play()
//    fun send() = receiver.receive(this)  // used for things like sending/triggering
//    fun receive(sender:Pattern) { println("WARNING: receive not implemented for $this") } // used for things like sending/triggering
//
//    val isEmpty: Boolean get() = children.none()
//
//    fun extend(vararg nodes:LanguageNode) { println("WARNING: extend not implemented for $this") } // used for things like triggering
//
//
//    // TODO: confirm this works!
//    fun stream(name:String, childLabel: NodeLabelInterface<*>, vararg values: Any?) {
//        val childrenIterator = children.iterator()
//        val valuesIterator = values.iterator()
//        while (valuesIterator.hasNext()) {
//            if (childrenIterator.hasNext()) {
//                childrenIterator.next().apply {
//                    node.properties[name] = valuesIterator.next()
//                    node.save()
//                }
//            } else {
//                extend(
//                    childLabel.create(properties = mapOf(name to valuesIterator.next()) )
//                )
//            }
//        }
//    }
//
//    // TODO: does this work??? Is it used? Naming?
//    fun setVein(name: String, vararg values:Any) {
//        children.zip(values.asSequence()).forEach { it.first.node.properties[name] = it.second }
//    }
//
//}






//
//// a node that represents an iterable over a group nodes ... each of which is connected
//// to this node, in a "pattern"
//interface Pattern: Node {
//
//    val isAlter: Boolean
//
//    val isLeaf: Boolean
//
//    val branches: SelectNodes<out Pattern>
//
//    val nodes: SelectNodes<out Tree>
//
//    val leaves: SelectNodes<out Tree>
//
//    // set to an instance of CuePath if this node is created in the context of a TreeSelect
//    var cuePath: CuePath?
//
////    // TODO: testing!
////    //TODO: below assumes that all ancestor properties should carry down... are we sure that's what we want?
////    val propertiesUp: Map<String, Any?> get() = properties + cuePath?.properties.orEmpty()
////
////    fun <T>getUp(name:String):T = propertiesUp[name] as T
////
//////    fun <T:Item>getSelect(select: Select<T> = Select(context=context, selectFrom=this.selectSelf) ) {
//////    }
////
////    fun saveDown() {
////        nodes.forEach { save() }
////    }
////
//////    fun <T>vein(name: String): Sequence<T> = leaves.asSequence().map { it.properties[key] as T }
////
////    // TODO: does this work???
////    fun setVein(name: String, vararg values:Any) {
////        leaves.asSequence().zip(values.asSequence()).forEach { it.first[key] = it.second }
////    }
////
////    // TODO: implement
////    // abstract val parents: SelectInterface
////
////    // TODO: implement the below
//////    # TODO: assume this doesn't need to be serialized?
//////    leaf_hooks: Iterable[Callable[["rain.Pattern", "rain.Pattern"], "rain.Pattern"]] = ()
//////    vein_hooks: Iterable[Callable[["rain.Pattern", Any, int], Any]] = ()
//////    _parentage = ()
//////    # TODO: MAYBE consider this
//////    # node_hooks: Iterable[Callable[["rain.Pattern", "rain.Pattern"], "rain.Pattern"]] = ()
//////
////}



