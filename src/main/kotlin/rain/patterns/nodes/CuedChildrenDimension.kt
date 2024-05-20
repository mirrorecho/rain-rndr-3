package rain.patterns.nodes

import rain.language.*
import rain.language.interfaces.*
import rain.patterns.interfaces.*

import rain.patterns.relationships.*

class CuedChildrenDimension(
    override val pattern:Pattern,
): Dimension {
    companion object : DimensionCompanion {
        override val label: DimensionLabel = DimensionLabel.CHILDREN
        override val factory: DimensionFactory = { p-> CuedChildrenDimension(p)}
    }

    override fun copy(anotherPattern: Pattern): Dimension = CuedChildrenDimension(anotherPattern)

//    override fun makeHistory(node:LanguageNode) = makeHistoryCopyingDimensions(node)

    override val label: DimensionLabel = Companion.label

    private fun getChildCues(qCue: SelectNodes): Sequence<Pattern> = sequence {
        this@CuedChildrenDimension.selectAny(qCue[CUES()]).forEach {
            yield(it)
            yieldAll(getChildCues(qCue[CUES_NEXT()]))
        }
    }

    override fun invoke() = getChildCues(pattern.node[CUES_FIRST()])

    override fun extend(vararg nodes: LanguageNode) {
        // creates all Cue nodes for the extension (inc. Contains and Cues relationships)
        val cues = nodes.map { childNode ->
            Cue.create().also { cue ->
                pattern.node.relate(CONTAINS, cue)
                cue.relate(CUES, childNode)
            }
        }

        if (this().none())
        // if empty, then create the CuesFirst
        // note... empty check works even after creating the Contains relationships above
        // because the isEmpty logic checks for CUES_FIRST
            CUES_FIRST.create(pattern.node.key, cues[0].key)
        else {
            // otherwise create a CuesNext relationship from the existing CuesLast target node to the start of extension cue nodes
            // and remove the CuesLast
            pattern.node.relates(CUES_LAST)(CUES_LAST).first().let {
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

