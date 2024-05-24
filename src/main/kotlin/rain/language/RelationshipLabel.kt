package rain.language

import rain.graph.interfaceable.GraphableRelationship
import rain.language._bak2.SelectRelationships
import rain.language.interfacing.RelationshipLabelInterface
import rain.language._bak2.RelationshipSelectable
import rain.language._bak2.SelectDirection
import rain.language.interfacing.NodeLabel
import rain.utils.autoKey

class RelationshipLabel(
    override val labelName:String,
    val direction: SelectDirection = SelectDirection.RIGHT,
): RelationshipSelectable, RelationshipLabelInterface<Relationship> {
    val left =
        if (direction== SelectDirection.RIGHT)
            RelationshipLabel(labelName, SelectDirection.LEFT)
        else
            this

    override val selectMe get() = SelectRelationships(labelName=labelName, direction = direction)

    override var context: Context = LocalContext

    override fun toString() = labelName

    fun factory(sourceKey:String, targetKey:String, key:String): Relationship {
        return Relationship(key, this, sourceKey, targetKey)
    }

    fun from(gRelationship: GraphableRelationship):T = gRelationship.run {
        factory(source.key, target.key, key).also { it.updatePropertiesFrom(this) }
    }

    // TODO: is all this complexity necessary for relationships???

    fun merge(
        sourceKey:String,
        targetKey:String,
        key:String,
        properties:Map<String,Any?>?=null,
        blockBefore:( LanguageRelationship.()->Unit )?=null,
        blockAfter:( LanguageRelationship.()->Unit )?=null
    ):LanguageRelationship =
        factory(sourceKey, targetKey, key).apply {
            properties?.let{ this.properties.putAll(it) };
            blockBefore?.invoke(this);
            context.graph.merge(this);
            blockAfter?.invoke(this);
        }

    fun create(
        sourceKey:String,
        targetKey:String,
        key:String = autoKey(),
        properties:Map<String,Any?>?=null,
        blockBefore:( LanguageRelationship.()->Unit )?=null,
        blockAfter:( LanguageRelationship.()->Unit )?=null
    ):LanguageRelationship =
        factory(sourceKey, targetKey, key).apply {
            properties?.let{ this.properties.putAll(it) };
            blockBefore?.invoke(this);
            context.graph.create(this);
            blockAfter?.invoke(this);
        }

    operator fun invoke(keys:List<String>?=null, properties: Map<String, Any>?= null, label: NodeLabel<*>?=null) =
        selectMe.nodes(keys, properties, label?.labelName)

    operator fun invoke(vararg keys:String, label: NodeLabel<*>?=null) =
        invoke(keys.asList(), null, label)

    operator fun invoke(properties: Map<String, Any>?= null, label: NodeLabel<*>?=null) =
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