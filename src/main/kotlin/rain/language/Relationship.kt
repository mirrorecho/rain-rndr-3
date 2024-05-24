package rain.language

import rain.graph.interfaceable.GraphableRelationship
import rain.language._bak2.SelectRelationships
import rain.language.interfacing.queries.Query
import rain.language.interfacing.queries.Queryable
import rain.utils.autoKey

final class Relationship(
    key:String = autoKey(),
    override val label: RelationshipLabel,
    var sourceKey: String,
    var targetKey: String,
): Queryable, GraphableRelationship, Item(key) {

    fun save() = graph.save(this)

    fun read() = graph.read(this)

    fun delete() = graph.deleteRelationship(this.key)

    override val context get() = label.context
    override val graph get() = context.graph

    override val queryMe: Query get() = SelectRelationships(keys=listOf(this.key))
    override val labelName get() = label.labelName

    override fun toString():String = "(${source.key} $labelName ${target.key} | $key) $properties"

    override val source: Node by lazy { Node.get(sourceKey) }
    override val target: Node by lazy { Node.get(targetKey) }

}

fun relate(
    sourceKey: String,
    rLabel:RelationshipLabel,
    targetKey:String,
    key:String = autoKey()
):Relationship = rLabel.create(sourceKey, targetKey, key)

fun relate(
    sourceNode: Node,
    rLabel:RelationshipLabel,
    targetNode:Node,
    key:String = autoKey()
):Relationship = rLabel.create(sourceNode.key, targetNode.key, key)