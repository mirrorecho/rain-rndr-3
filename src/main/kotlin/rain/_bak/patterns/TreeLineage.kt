package rain._bak.patterns

//import rain.language.interfacing.NodeLabel
//import rain.language._bak2.SelectNodes
//import rain.patterns.relationships.CUES
//import rain.patterns.relationships.CUES_FIRST
//import rain.patterns.relationships.CUES_NEXT
//
//open class TreeLineage<T:Tree, TL:TreeLineage<T,TL>>(
//    val tree: T,
//    val label: NodeLabel<out T>,
//    val cue: Cue? = null,
//    val parent: TL? = null,
//) {
//    // TODO: implement caching!
//
//    //TODO: below assumes that all ancestor properties should carry down... are we sure that's what we want?
//    val properties get() = tree.properties
//    val lineageProperties: Map<String, Any?> by lazy { (parent?.lineageProperties.orEmpty() + properties).toMutableMap() }
//
//    override fun toString() = "TREE LINEAGE: ${ancestors.toList().reversed().map { it.tree.key }} - ${tree.labelName}(${tree.key}) $lineageProperties"
//
//    fun <T>getAs(name:String):T = lineageProperties[name] as T
//
//    operator fun get(name:String) = lineageProperties[name]
//
//    open val thisAs: TL get() = this as TL
//
//    private fun getChildren(qCue:SelectNodes):Sequence<TL> = sequence {
//        qCue.first(Cue)?.let {cue ->
//            TreeLineage(cue[CUES()](label).first(), label, cue, thisAs).let {
//                yield(it.thisAs)
//                yieldAll(getChildren(cue[CUES_NEXT()]))
//            }
//        }
//    }
//
//    val children get() = getChildren(tree[CUES_FIRST()])
//
//    val nodes:Sequence<TL> get() = sequence {
//        yield(thisAs)
//        children.forEach { yieldAll(it.nodes) }
//    }
//
//    val leaves:Sequence<TL> get() = sequence {
//        if (tree.isLeaf) yield(thisAs)
//        else children.forEach { yieldAll(it.leaves) }
//    }
//
//    val ancestors:Sequence<TL> get() = sequence {
//        parent?.let{ yield(it); yieldAll(it.ancestors); }
//    }
//
//    val previous get() = cue?.let{ it[CUES_NEXT.left(), CUES()].first(label) }
//    val next get() = cue?.let{ it[CUES_NEXT(), CUES()].first(label) }
//
//}