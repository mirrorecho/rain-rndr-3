package rain.patterns.nodes

import rain.interfaces.*
import rain.language.*
import rain.machines.nodes.Machine

import rain.patterns.relationships.*


class TriggeringTree(
    node: LanguageNode,
    historyPattern: PatternInterface? = null,
    historyDimension: PatternInterface.Dimension? = null,
): Pattern(node, historyPattern, historyDimension) {

    private fun getCuedPatterns(qCue: SelectNodes): Sequence<PatternInterface> = sequence {
        this@TriggeringTree.selectAny(qCue[CUES()], PatternInterface.Dimension.CHILDREN).forEach {
            yield(it)
            yieldAll(getCuedPatterns(qCue[CUES_NEXT()]))
        }
    }

    override operator fun get(dimension: PatternInterface.Dimension) = when (dimension) {
        PatternInterface.Dimension.CHILDREN ->
            getCuedPatterns(node[CUES_FIRST()]);
        PatternInterface.Dimension.BUMPS ->
            sequence {
                super.get(PatternInterface.Dimension.HISTORY).forEach { h ->
                    h.selectFrom(TRIGGERS(), PatternInterface.Dimension.BUMPS).forEach { t ->
                        properties.manageWith(Event.EventManager()).machinePath.let {mp->
                            if (mp.isNullOrEmpty()) yield(t)
                            else yieldAll(
                                h.selectAny(
                                    t.node.get( *(mp.map { r -> r() }.toTypedArray()) ),
                                    PatternInterface.Dimension.BUMPS
                                )
                            )
                        }
                    }
                }
            }
        else -> super.get(dimension)
    }

//    override fun send() {
//
//    }


    // COMBINED PROPERTIES FROM ALL PARENTS (last parents take priority)
//    private val parentsProperties: MutableMap<String, Any?> =
//        parents.map { it.properties }.fold(mutableMapOf<String, Any?>()) { acc, map ->
//            acc.apply { putAll(map) }
//        }

    // TODO: it's odd and annoying to convert this to a mutable map... but maybe it's unavoidable?
    override val properties = (historyPattern?.properties.orEmpty() + node.properties).toMutableMap()

    fun extendChildren(vararg nodes: LanguageNode) {
        // creates all Cue nodes for the extension (inc. Contains and Cues relationships)
        val cues = nodes.map { childNode ->
            Cue.create().also { cue ->
                this.node.relate(CONTAINS, cue)
                cue.relate(CUES, childNode)
            }
        }

        if (this[PatternInterface.Dimension.CHILDREN].none())
        // if empty, then create the CuesFirst
        // note... empty check works even after creating the Contains relationships above
        // because the isEmpty logic checks for CUES_FIRST
            CUES_FIRST.create(node.key, cues[0].key)
        else {
            // otherwise create a CuesNext relationship from the existing CuesLast target node to the start of extension cue nodes
            // and remove the CuesLast
            node.relates(CUES_LAST)(CUES_LAST).first().let {
                CUES_NEXT.create(it.targetKey, cues[0].key)
                it.delete()
            }
        }

        // creates CuesNext relationships between all the Cue nodes
        cues.asIterable().zipWithNext { c, cNext ->
            CUES_NEXT.create(c.key, cNext.key)
        }

        // adds CuesLast relationship at the end
        CUES_LAST.create(node.key, cues.last().key)
    }

    override fun extend(dimension: PatternInterface.Dimension, vararg nodes: LanguageNode) {
        when (dimension) {
            PatternInterface.Dimension.CHILDREN -> extendChildren(*nodes)
            else -> super.extend(dimension, *nodes)
        }
    }
}

//
//
//abstract class Tree(
//    key:String = autoKey(),
//): Node(key) {
////    companion object : NodeLabel<Tree>(Tree::class, Node, { k -> Tree(k) })
//    abstract override val label: NodeLabel<out Tree4>
//
//    val isAlter = false
//
//    var isLeaf: Boolean by this.properties.apply { putIfAbsent("isLeaf", false) }
//
//    abstract val lineage: TreeLineage<out Tree4>
//
//    // TODO: needed?
////    abstract val lineageProperties: List<String>
//
//    // replaced with cuePath below
////    override var cachedParentage = listOf<Tree>()
//
////    fun <T:Item>getSelect(select: Select<T> = Select(context=context, selectFrom=this.selectSelf) ) {
////    }
//
//
//
//
//
//
//    // TODO: implement
//    // abstract val parents: SelectInterface
//
//    // TODO: implement the below
////    # TODO: assume this doesn't need to be serialized?
////    leaf_hooks: Iterable[Callable[["rain.Pattern", "rain.Pattern"], "rain.Pattern"]] = ()
////    vein_hooks: Iterable[Callable[["rain.Pattern", Any, int], Any]] = ()
////    _parentage = ()
////    # TODO: MAYBE consider this
////    # node_hooks: Iterable[Callable[["rain.Pattern", "rain.Pattern"], "rain.Pattern"]] = ()
////
//
//
//}
