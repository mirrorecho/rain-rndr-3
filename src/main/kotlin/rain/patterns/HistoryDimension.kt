package rain.patterns

import rain.graph.interfacing.GraphableNode

class HistoryDimension(
    pattern: Pattern,
): Dimension(pattern, DimensionLabel.HISTORY) {

    override val graphableNodes:Sequence<GraphableNode> = sequence {
        yield(pattern.node); yieldAll(pattern.history.graphableNodes);
    }

}