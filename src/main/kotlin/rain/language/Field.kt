package rain.language

import rain.patterns.*

//interface StaticField<T:Any> {
//    val name: String
//
//    fun attach(node:Node, previous:Pattern<*>?=null): ConnectedField<T, CN>
//
//}

interface Field<T:Any, SN:Node, CN:Node> {
    val name: String
    val sourceLabel:NodeLabel<SN>
    val connectedLabel:NodeLabel<CN>
    val patternFactory: ((source:Node, previous:Pattern<*>?)->Pattern<CN>)? // TODO: maybe could be Pattern<SN>?
    val defaultToSelf:Boolean

     fun attach(
         node:SN,
         previous:Pattern<*>?=null
     ): AttachedField<T, SN, CN>

 }

// =================================

open class LocalValueField<T:Any, N:Node>(
    override val name: String,
    label:NodeLabel<N>
): Field<T, N, N> {

    override val sourceLabel:NodeLabel<N> = label
    override val connectedLabel:NodeLabel<N> = label
    override val patternFactory = null
    override val defaultToSelf:Boolean = true

    override fun attach(node:N, previous:Pattern<*>?): AttachedLocalValueField<T, N> =
        AttachedLocalValueField(this, node)
}

open class DefaultingLocalValueField<T:Any, N:Node>(
    name: String,
    label:NodeLabel<N>,
    val default: T,
): LocalValueField<T, N>(name, label) {

    override fun attach(node:N, previous:Pattern<*>?): AttachedDefaultingLocalValueField<T, N> =
        AttachedDefaultingLocalValueField(this, node)
}

// =================================

open class ValueField<T:Any, SN:Node, CN:Node>(
    override val name: String,
    override val sourceLabel:NodeLabel<SN>,
    override val connectedLabel:NodeLabel<CN>,
    override val patternFactory: (source:Node, previous:Pattern<*>?)->Pattern<CN>,
    var defaultConnectFieldName: String? = null,
    override val defaultToSelf:Boolean = true,
): Field<T, SN, CN> {
    override fun attach(node:SN, previous:Pattern<*>?): AttachedValueField<T, SN, CN> =
        AttachedValueField(this, patternFactory(node, previous))
}

class DefaultingValueField<T:Any, SN:Node, CN:Node>(
    name: String,
    sourceLabel:NodeLabel<SN>,
    connectedLabel:NodeLabel<CN>,
    patternFactory: (source:Node, previous:Pattern<*>?)->Pattern<CN>,
    val default: T,
    defaultConnectFieldName: String? = null,
    defaultToSelf:Boolean = true,
): ValueField<T, SN, CN>(name, sourceLabel, connectedLabel, patternFactory, defaultConnectFieldName, defaultToSelf) {
    override fun attach(node:SN, previous:Pattern<*>?): AttachedDefaultingValueField<T, SN, CN> =
        AttachedDefaultingValueField(this, patternFactory(node, previous))
}

// =================================

open class NodeField<T:Node, SN:Node>(
    override val name: String,
    override val sourceLabel:NodeLabel<SN>,
    override val connectedLabel:NodeLabel<T>,
    override val patternFactory: (source:Node, previous:Pattern<*>?)->Pattern<T>,
    override val defaultToSelf:Boolean = true,
): Field<T, SN, T>{
    override fun attach(node:SN, previous:Pattern<*>?): AttachedNodeField<T, SN> =
        AttachedNodeField(this, patternFactory(node, previous))
}

class DefaultingNodeField<T:Node, SN:Node>(
    name: String,
    sourceLabel:NodeLabel<SN>,
    connectedLabel:NodeLabel<T>,
    patternFactory: (source:Node, previous:Pattern<*>?)->Pattern<T>,
    val default: T,
    defaultToSelf:Boolean = true,
): NodeField<T, SN>(name, sourceLabel, connectedLabel, patternFactory, defaultToSelf) {
    override fun attach(node:SN, previous:Pattern<*>?): AttachedDefaultingNodeField<T, SN> =
        AttachedDefaultingNodeField(this, patternFactory(node, previous))
}

// ======================================================================

// factory for LocalValueField
fun <T:Any, N:Node>NodeLabel<N>.field(
    name:String,
) = LocalValueField<T, N>(
    name,
    this
)

// factory for DefaultingLocalValueField
fun <T:Any, N:Node>NodeLabel<N>.field(
    name:String,
    default: T,
) = DefaultingLocalValueField(
    name,
    this,
    default
)
// ======================================================================

// factory for ValueField by relationship:
fun <T:Any, SN:Node, CN:Node>NodeLabel<SN>.field(
    name:String,
    connectedLabel:NodeLabel<CN>,
    relationshipLabel: RelationshipLabel,
    defaultConnectFieldName: String? = null,
    defaultToSelf: Boolean=true,
) = ValueField<T, SN, CN>(
    name,
    this,
    connectedLabel,
    {s, p-> RelatesPattern(s, connectedLabel, p, relationshipLabel)},
    defaultConnectFieldName,
    defaultToSelf
)

// factory for ValueField by pattern factory:
fun <T:Any, SN:Node, CN:Node>NodeLabel<SN>.field(
    name:String,
    connectedLabel:NodeLabel<CN>,
    patternFactory: (source:Node, previous:Pattern<*>?)->Pattern<CN>,
    defaultConnectFieldName: String? = null,
    defaultToSelf: Boolean=true,
) = ValueField<T, SN, CN>(
    name,
    this,
    connectedLabel,
    patternFactory,
    defaultConnectFieldName,
    defaultToSelf
)

// factory for DefaultingValueField by relationship:
fun <T:Any, SN:Node, CN:Node>NodeLabel<SN>.field(
    name:String,
    connectedLabel:NodeLabel<CN>,
    relationshipLabel: RelationshipLabel,
    default:T,
    defaultConnectFieldName: String? = null,
    defaultToSelf: Boolean=true
) = DefaultingValueField(
    name,
    this,
    connectedLabel,
    {s, p-> RelatesPattern(s, connectedLabel, p, relationshipLabel)},
    default,
    defaultConnectFieldName,
    defaultToSelf
)

// factory for DefaultingValueField pattern factory:
fun <T:Any, SN:Node, CN:Node>NodeLabel<SN>.field(
    name:String,
    connectedLabel:NodeLabel<CN>,
    patternFactory: (source:Node, previous:Pattern<*>?)->Pattern<CN>,
    default:T,
    defaultConnectFieldName: String? = null,
    defaultToSelf: Boolean=true
) = DefaultingValueField(
    name,
    this,
    connectedLabel,
    patternFactory,
    default,
    defaultConnectFieldName,
    defaultToSelf
)

// ===============================

// factory for NodeField by relationship:
fun <T:Node, SN:Node>NodeLabel<SN>.nodeField(
    name:String,
    connectedLabel:NodeLabel<T>,
    relationshipLabel: RelationshipLabel,
    defaultToSelf: Boolean=true,
) = NodeField(
    name,
    this,
    connectedLabel,
    {s, p-> RelatesPattern(s, connectedLabel, p, relationshipLabel)},
    defaultToSelf
)

// factory for NodeField by pattern factory:
fun <T:Node, SN:Node>NodeLabel<SN>.nodeField(
    name:String,
    connectedLabel:NodeLabel<T>,
    patternFactory: (source:Node, previous:Pattern<*>?)->Pattern<T>,
    defaultToSelf: Boolean=true,
) = NodeField(
    name,
    this,
    connectedLabel,
    patternFactory,
    defaultToSelf
)

// factory for DefaultingNodeField by relationship:
fun <T:Node, SN:Node>NodeLabel<SN>.nodeField(
    name:String,
    connectedLabel:NodeLabel<T>,
    relationshipLabel: RelationshipLabel,
    default:T,
    defaultToSelf: Boolean=true
) = DefaultingNodeField(
    name,
    this,
    connectedLabel,
    {s, p-> RelatesPattern(s, connectedLabel, p, relationshipLabel)},
    default,
    defaultToSelf
)

// factory for DefaultingNodeField pattern factory:
fun <T:Node, SN:Node>NodeLabel<SN>.nodeField(
    name:String,
    connectedLabel:NodeLabel<T>,
    patternFactory: (source:Node, previous:Pattern<*>?)->Pattern<T>,
    default:T,
    defaultToSelf: Boolean=true
) = DefaultingNodeField(
    name,
    this,
    connectedLabel,
    patternFactory,
    default,
    defaultToSelf
)

// ======================================================================================

fun <T:Any, N:Node, F:LocalValueField<T, N>>N.attachField(field: F, previous:Pattern<*>?=null): AttachedLocalValueField<T, N> {
    return field.attach(this, previous).also {
        attachedFields[field.name] = it
    }
}

fun <T:Any, N:Node, F:DefaultingLocalValueField<T, N>>N.attachField(field: F, previous:Pattern<*>?=null): AttachedDefaultingLocalValueField<T, N> {
    return field.attach(this, previous).also {
        attachedFields[field.name] = it
    }
}

fun <T:Any, SN:Node, CN:Node, F:ValueField<T, SN, CN>>SN.attachField(field: F, previous:Pattern<*>?=null): AttachedValueField<T, SN, CN> {
    return field.attach(this, previous).also {
        attachedFields[field.name] = it
    }
}

fun <T:Any, SN:Node, CN:Node, F:DefaultingValueField<T, SN, CN>>SN.attachField(field: F, previous:Pattern<*>?=null): AttachedDefaultingValueField<T, SN, CN> {
    return field.attach(this, previous).also {
        attachedFields[field.name] = it
    }
}

fun <T:Node, SN:Node, F:NodeField<T, SN>>SN.attachField(field: F, previous:Pattern<*>?=null): AttachedNodeField<T, SN> {
    return field.attach(this, previous).also {
        attachedFields[field.name] = it
    }
}

fun <T:Node, SN:Node, F:DefaultingNodeField<T, SN>>SN.attachField(field: F, previous:Pattern<*>?=null): AttachedDefaultingNodeField<T, SN> {
    return field.attach(this, previous).also {
        attachedFields[field.name] = it
    }
}