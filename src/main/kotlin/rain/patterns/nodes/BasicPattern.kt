package rain.patterns.nodes

import rain.language.interfaces.*
import rain.patterns.interfaces.*

open class BasicPattern(
    override val node: LanguageNode,
    override val historyPattern: Pattern? = null,
    override val historyDimension: PatternDimension? = null,
): Pattern {
    override fun dimensionPatternFactory(dimension: PatternDimension): (LanguageNode) -> Pattern =
        {n->BasicPattern(n, this@BasicPattern, dimension) }
}