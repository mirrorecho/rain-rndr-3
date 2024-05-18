package rain.language

import rain.language.interfaces.Context
import rain.language.interfaces.RelationshipLabelInterface
import rain.language.interfaces.RelationshipSelectable
import rain.language.interfaces.SelectDirection

class RelationshipLabel(
    override val labelName:String,
    val direction: SelectDirection = SelectDirection.RIGHT,
): RelationshipSelectable, RelationshipLabelInterface<Relationship> {
    val left =
        if (direction==SelectDirection.RIGHT)
            RelationshipLabel(labelName, SelectDirection.LEFT)
        else
            this

    override val selectMe get() = SelectRelationships(labelName=labelName, direction = direction)
    override val allNames: List<String> = listOf(labelName)

    override var context: Context = LocalContext

    override fun toString() = labelName

    override fun factory(sourceKey:String, targetKey:String, key:String): Relationship {
        return Relationship(key, this, sourceKey, targetKey)
    }

    operator fun invoke(keys:List<String>?=null, properties: Map<String, Any>?= null, label:NodeLabel<*>?=null) =
        selectMe.nodes(keys, properties, label?.labelName)

    operator fun invoke(vararg keys:String, label:NodeLabel<*>?=null) =
        invoke(keys.asList(), null, label)

    operator fun invoke(properties: Map<String, Any>?= null, label:NodeLabel<*>?=null) =
        invoke(null, properties, label)

//    fun left(keys:List<String>?=null, properties: Map<String, Any>?= null, label:NodeLabel<*>?=null) =
//        SelectRelationships(labelName=this.labelName, direction= SelectDirection.LEFT).nodes(keys, properties, label?.labelName)
//
//    fun left(vararg keys:String, label:NodeLabel<*>?=null) =
//        left(keys.asList(), null, label)
//
//    fun left(properties: Map<String, Any>?= null, label:NodeLabel<*>?=null) =
//        left(null, properties, label)

}

val TARGETS = RelationshipLabel("TARGETS")