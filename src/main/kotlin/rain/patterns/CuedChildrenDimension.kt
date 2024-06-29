package rain.patterns

import rain.graph.interfacing.GraphableNode
import rain.language.Query
import rain.language.Context
import rain.language.Node
import rain.patterns.nodes.Cue

import rain.patterns.relationships.*

class CuedChildrenDimension(
    pattern: Pattern,
): Dimension(pattern, DimensionLabel.CHILDREN) {
    companion object : DimensionCompanion {
        override val label: DimensionLabel = DimensionLabel.CHILDREN
        override val factory: DimensionFactory = { p-> CuedChildrenDimension(p) }
    }

    override val context: Context get() = pattern.node.context

//    override var fromQuery: Query? = pattern.historyDimension  // TODO maybe: is this a reasonable way to handle history?


    override val label: DimensionLabel = Companion.label

    private fun getChildCues(qCue: Query): Sequence<GraphableNode> = sequence {
        qCue[CUES()].graphableNodes.forEach {
            yield(it)
            yieldAll(getChildCues(qCue[CUES_NEXT()]))
        }
    }

    override val graphableNodes = getChildCues(pattern.node[CUES_FIRST()])

    override fun extend(vararg nodes: Node) {
        // creates all Cue nodes for the extension (inc. Contains and Cues relationships)
        val cues = nodes.map { childNode ->
            Cue.create().also { cue ->
                pattern.node.relate(CONTAINS, cue)
                cue.relate(CUES, childNode)
            }
        }

        if (graphableNodes.none())
        // if empty, then create the CuesFirst
        // note... empty check works even after creating the Contains relationships above
        // because the isEmpty logic checks for CUES_FIRST
            CUES_FIRST.create(pattern.node.key, cues[0].key)
        else {
            // otherwise create a CuesNext relationship from the existing CuesLast target node to the start of extension cue nodes
            // and remove the CuesLast
            pattern.node.getRelationships(CUES_LAST).first().also {
                CUES_NEXT.create(it.targetKey, cues[0].key)
                it.delete()
            }

        }

        // creates CuesNext relationships between all the Cue nodes
        cues.asIterable().zipWithNext { c, cNext ->
            CUES_NEXT.create(c.key, cNext.key)
        }

        // adds CuesLast relationship at the end
        CUES_LAST.create(pattern.node.key, cues.last().key)
    }


}

