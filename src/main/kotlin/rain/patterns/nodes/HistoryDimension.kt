package rain.patterns.nodes

import rain.patterns.interfaces.Dimension
import rain.patterns.interfaces.DimensionLabel
import rain.patterns.interfaces.Pattern

class HistoryDimension(override val pattern: Pattern): Dimension {

    override val label: DimensionLabel = DimensionLabel.HISTORY

    override fun copy(anotherPattern: Pattern): Dimension = HistoryDimension(anotherPattern)

    override fun invoke(): Sequence<Pattern> =
            sequence { pattern.historyPattern?.let { yield(it); yieldAll(it[DimensionLabel.HISTORY]()) } }
}