package rain.language.fields

import rain.language.*
import rain.patterns.*

interface Field<T:Any?> {
    val name: String
    val isNode: Boolean
    val defaultToSelf:Boolean
    val default: T
    val cascade: Boolean // TODO: reconsider cascade defaults (after playing with this with solves)

     fun attach(
         node: Node,
         previous:Pattern<*>?=null
     ): AttachedField<T>
 }

// =================================

open class LocalValueField<T:Any?>(
    override val name: String,
    override val default: T,
    override val cascade: Boolean = true
): Field<T> {

    override val isNode: Boolean = false
    override val defaultToSelf:Boolean = true

    override fun attach(node: Node, previous:Pattern<*>?): AttachedLocalValue<T> =
        AttachedLocalValue(this, node)
}


// ===========================================================================

interface ConnectingField<T:Any?>: Field<T> {
    abstract val patternFactory: (source: Node, previous:Pattern<*>?)->Pattern<*>
}

// =================================

// TODO: below causes all kinds of generics problems. WHY?
//  Instead needing to create ConnectingNodeField and ConnectingNodeDefaultingField
// open class ConnectingNodeField<NT:Node, T:NT?>(

open class ConnectingNodeField<T: Node>(
    override val name: String,
    override val patternFactory: (source: Node, previous:Pattern<*>?)->Pattern<*>,
    val label: NodeLabel<T>,
    override val default: T?,
    override val cascade: Boolean = true
): ConnectingField<T?>, Field<T?> {

    override val isNode: Boolean = true
    override val defaultToSelf:Boolean = false

    override fun attach(node: Node, previous:Pattern<*>?): AttachedConnectingNode<T> =
        AttachedConnectingNode(this, patternFactory(node, previous))
}

open class ConnectingDefaultingNodeField<T: Node>(
    override val name: String,
    override val patternFactory: (source: Node, previous:Pattern<*>?)->Pattern<*>,
    val label: NodeLabel<T>,
    override val default: T,
    override val cascade: Boolean = true
): ConnectingField<T>, Field<T> {

    override val isNode: Boolean = true
    override val defaultToSelf:Boolean = false

    override fun attach(node: Node, previous:Pattern<*>?): AttachedConnectingDefaultingNode<T> =
        AttachedConnectingDefaultingNode(this, patternFactory(node, previous))
}

// =================================

open class ConnectingValueField<T:Any?>(
    override val name: String,
    override val patternFactory: (source: Node, previous:Pattern<*>?)->Pattern<*>,
    override val default: T,
    override val cascade: Boolean = true,
    override val defaultToSelf:Boolean = true
): ConnectingField<T>, Field<T> {

    override val isNode: Boolean = true
    override fun attach(node: Node, previous:Pattern<*>?): AttachedConnectingValue<T> =
        AttachedConnectingValue(this, patternFactory(node, previous))

}


//// ======================================================================
//
fun <T:Any?> field(name: String, default: T? = null, cascade: Boolean = true) =
    LocalValueField(name, default, cascade)

fun <T:Any> field(name: String, default: T, cascade: Boolean = true) =
    LocalValueField(name, default, cascade)

fun <T:Any?> field(
    name: String,
    relationshipLabel: RelationshipLabel,
    default: T? = null,
    cascade: Boolean = true,
    defaultToSelf: Boolean=true
) =
    ConnectingValueField(
        name,
        {s, p-> RelatesPattern(s, p, relationshipLabel)},
        default,
        cascade,
        defaultToSelf
    )

fun <T:Any> field(
    name: String,
    relationshipLabel: RelationshipLabel,
    default: T,
    cascade: Boolean = true,
    defaultToSelf: Boolean=true
) =
    ConnectingValueField(
        name,
        {s, p-> RelatesPattern(s, p, relationshipLabel)},
        default,
        cascade,
        defaultToSelf
    )


fun <T: Node> field(
    name: String,
    relationshipLabel: RelationshipLabel,
    label: NodeLabel<T>,
    default: T? = null,
    cascade: Boolean = true,
) =
    ConnectingNodeField(
        name,
        {s, p-> RelatesPattern(s, p, relationshipLabel)},
        label,
        default,
        cascade,
    )

fun <T: Node> field(
    name: String,
    relationshipLabel: RelationshipLabel,
    label: NodeLabel<T>,
    default: T,
    cascade: Boolean = true
) =
    ConnectingDefaultingNodeField(
        name,
        {s, p-> RelatesPattern(s, p, relationshipLabel)},
        label,
        default,
        cascade
    )

//
//fun <T:Node> nodeField(name: String, patternFactory: (source:Node, previous:Pattern<*>?)->Pattern<*>, default: T) =
//    ConnectingNodeField(name, patternFactory, default)
//
//fun <T:Node?> nodeField(name: String, relationshipLabel: RelationshipLabel, default: T? = null) =
//    ConnectingNodeField(name,  {s, p-> RelatesPattern(s, p, relationshipLabel)}, default)
//
//fun <T:Node> nodeField(name: String, relationshipLabel: RelationshipLabel, default: T) =
//    ConnectingNodeField(name,  {s, p-> RelatesPattern(s, p, relationshipLabel)}, default)

// ======================================================================



//)
//
//// factory for DefaultingLocalValueField
//fun <T:Any, N:Node>NodeLabel<N>.field(
//    name:String,
//    default: T,
//) = DefaultingLocalValueField(
//    name,
//    this,
//    default
//)
//// ======================================================================
//
//// factory for ValueField by relationship:
//fun <T:Any, SN:Node, CN:Node>NodeLabel<SN>.field(
//    name:String,
//    connectedLabel:NodeLabel<CN>,
//    relationshipLabel: RelationshipLabel,
//    defaultConnectFieldName: String? = null,
//    defaultToSelf: Boolean=true,
//) = ValueField<T, SN, CN>(
//    name,
//    this,
//    connectedLabel,
//    {s, p-> RelatesPattern(s, connectedLabel, p, relationshipLabel)},
//    defaultConnectFieldName,
//    defaultToSelf
//)
//
//// factory for ValueField by pattern factory:
//fun <T:Any, SN:Node, CN:Node>NodeLabel<SN>.field(
//    name:String,
//    connectedLabel:NodeLabel<CN>,
//    patternFactory: (source:Node, previous:Pattern<*>?)->Pattern<CN>,
//    defaultConnectFieldName: String? = null,
//    defaultToSelf: Boolean=true,
//) = ValueField<T, SN, CN>(
//    name,
//    this,
//    connectedLabel,
//    patternFactory,
//    defaultConnectFieldName,
//    defaultToSelf
//)
//
//// factory for DefaultingValueField by relationship:
//fun <T:Any, SN:Node, CN:Node>NodeLabel<SN>.field(
//    name:String,
//    connectedLabel:NodeLabel<CN>,
//    relationshipLabel: RelationshipLabel,
//    default:T,
//    defaultConnectFieldName: String? = null,
//    defaultToSelf: Boolean=true
//) = DefaultingValueField(
//    name,
//    this,
//    connectedLabel,
//    {s, p-> RelatesPattern(s, connectedLabel, p, relationshipLabel)},
//    default,
//    defaultConnectFieldName,
//    defaultToSelf
//)
//
//// factory for DefaultingValueField pattern factory:
//fun <T:Any, SN:Node, CN:Node>NodeLabel<SN>.field(
//    name:String,
//    connectedLabel:NodeLabel<CN>,
//    patternFactory: (source:Node, previous:Pattern<*>?)->Pattern<CN>,
//    default:T,
//    defaultConnectFieldName: String? = null,
//    defaultToSelf: Boolean=true
//) = DefaultingValueField(
//    name,
//    this,
//    connectedLabel,
//    patternFactory,
//    default,
//    defaultConnectFieldName,
//    defaultToSelf
//)
//
//// ===============================
//
//// factory for NodeField by relationship:
//fun <T:Node, SN:Node>NodeLabel<SN>.nodeField(
//    name:String,
//    connectedLabel:NodeLabel<T>,
//    relationshipLabel: RelationshipLabel,
//    defaultToSelf: Boolean=true,
//) = NodeField(
//    name,
//    this,
//    connectedLabel,
//    {s, p-> RelatesPattern(s, connectedLabel, p, relationshipLabel)},
//    defaultToSelf
//)
//
//// factory for NodeField by pattern factory:
//fun <T:Node, SN:Node>NodeLabel<SN>.nodeField(
//    name:String,
//    connectedLabel:NodeLabel<T>,
//    patternFactory: (source:Node, previous:Pattern<*>?)->Pattern<T>,
//    defaultToSelf: Boolean=true,
//) = NodeField(
//    name,
//    this,
//    connectedLabel,
//    patternFactory,
//    defaultToSelf
//)
//
//// factory for DefaultingNodeField by relationship:
//fun <T:Node, SN:Node>NodeLabel<SN>.nodeField(
//    name:String,
//    connectedLabel:NodeLabel<T>,
//    relationshipLabel: RelationshipLabel,
//    default:T,
//    defaultToSelf: Boolean=true
//) = DefaultingNodeField(
//    name,
//    this,
//    connectedLabel,
//    {s, p-> RelatesPattern(s, connectedLabel, p, relationshipLabel)},
//    default,
//    defaultToSelf
//)
//
//// factory for DefaultingNodeField pattern factory:
//fun <T:Node, SN:Node>NodeLabel<SN>.nodeField(
//    name:String,
//    connectedLabel:NodeLabel<T>,
//    patternFactory: (source:Node, previous:Pattern<*>?)->Pattern<T>,
//    default:T,
//    defaultToSelf: Boolean=true
//) = DefaultingNodeField(
//    name,
//    this,
//    connectedLabel,
//    patternFactory,
//    default,
//    defaultToSelf
//)
//
//// ======================================================================================
//
//fun <T:Any, N:Node, F:LocalValueField<T, N>>N.attachField(field: F, previous:Pattern<*>?=null): AttachedLocalValueField<T, N> {
//    return field.attach(this, previous).also {
//        attachedFields[field.name] = it
//    }
//}
//
//fun <T:Any, N:Node, F:DefaultingLocalValueField<T, N>>N.attachField(field: F, previous:Pattern<*>?=null): AttachedDefaultingLocalValueField<T, N> {
//    return field.attach(this, previous).also {
//        attachedFields[field.name] = it
//    }
//}
//
//fun <T:Any, SN:Node, CN:Node, F:ValueField<T, SN, CN>>SN.attachField(field: F, previous:Pattern<*>?=null): AttachedValueField<T, SN, CN> {
//    return field.attach(this, previous).also {
//        attachedFields[field.name] = it
//    }
//}
//
//fun <T:Any, SN:Node, CN:Node, F:DefaultingValueField<T, SN, CN>>SN.attachField(field: F, previous:Pattern<*>?=null): AttachedDefaultingValueField<T, SN, CN> {
//    return field.attach(this, previous).also {
//        attachedFields[field.name] = it
//    }
//}
//
//fun <T:Node, SN:Node, F:NodeField<T, SN>>SN.attachField(field: F, previous:Pattern<*>?=null): AttachedNodeField<T, SN> {
//    return field.attach(this, previous).also {
//        attachedFields[field.name] = it
//    }
//}
//
//fun <T:Node, SN:Node, F:DefaultingNodeField<T, SN>>SN.attachField(field: F, previous:Pattern<*>?=null): AttachedDefaultingNodeField<T, SN> {
//    return field.attach(this, previous).also {
//        attachedFields[field.name] = it
//    }
//}