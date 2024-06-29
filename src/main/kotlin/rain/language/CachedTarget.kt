package rain.language

import rain.graph.interfacing.QueryMethod
import rain.utils.Caching

// TODO: maybe this should inherit from, or be combined with, RelatesDimension
class CachedTarget<T: Node>(
    val sourceNode: Node, // TODO maybe, switch to var, with null default?
    val relationshipLabel: RelationshipLabel,
    val label: NodeLabel<T>,
): Query(
    QueryMethod.related(relationshipLabel.directionIsRight),
    relationshipLabel.labelName,
    queryFrom = sourceNode.queryMe
) {

    private val cachedQuery = this.TypedCached(label)

    private var cachedNode by Caching { cachedQuery.first }

    var target: T?
        get() = cachedNode
        set(node) {
            cachedNode = node
            invoke(label).firstOrNull()?.delete()
            node?.let { sourceNode.relate(relationshipLabel, it) }
        }

    fun createIfMissing() {
        if (cachedNode==null) {
            cachedNode = label.create().also { sourceNode.relate(relationshipLabel, it) }
        }
    }

}


//import rain.language._bak2.SelectDirection
//import rain.language.NodeLabel
//
//// TODO: move somewhere else
//class CachedTarget<T:Node>(
//    val sourceNode:Node,
//    val rLabel: RelationshipLabel,
//    val nLabel: NodeLabel<T>,
//    direction: SelectDirection = SelectDirection.RIGHT
//) {
//    val rQuery = sourceNode.relates(label=rLabel, direction)
//    val query = rQuery.nodes()
//    private var _cachedValue:T? = query(nLabel).firstOrNull()
//
//    var target: T? get() {
//        _cachedValue?.let { return it }
//        _cachedValue = query(nLabel).firstOrNull()
//        return _cachedValue
//    }
//        set(value) {
//            _cachedValue = value
//            rQuery(rLabel).firstOrNull()?.delete()
//            value?.let { sourceNode.relate(rLabel, it) }
//        }
//
//    fun createIfMissing() {
//        if (_cachedValue==null) {
//            _cachedValue = nLabel.create().also { sourceNode.relate(rLabel, it) }
//        }
//    }
//
//}