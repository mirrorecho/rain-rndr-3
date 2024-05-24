package rain.language._bak2

import rain.language.LocalContext
import rain.language.Node
import rain.language.Relationship
import rain.language.interfacing.*
import rain.language.Context

//fun <T: Node>Sequence<T>.select(): SelectNodes =
//    SelectNodes(keys = this.toList().map { it.key } )
//
//fun <T: Relationship>Sequence<T>.select(): SelectRelationships =
//    SelectRelationships(keys = this.toList().map { it.key })
//
//fun Sequence<String>.selectNodes(): SelectNodes =
//    SelectNodes(keys = this.toList())
//
//fun List<String>.selectNodes(): SelectNodes =
//    SelectNodes(keys = this)
//
//fun select(
//    vararg keys:String,
//    properties:Map<String,Any>?=null,
//    labelName: String? = null,
//) = SelectNodes(keys.asList(), properties, labelName)

open class SelectNodesOld(
    override val keys: List<String>? = null,
    override val properties: Map<String, Any?>? = null,
    override val labelName: String? = null,
    override var selectFrom: SelectInterface? = null,
    override var context: Context = LocalContext // TODO maybe: explicitly pass context along when selecting?
    ): SelectInterface, NodeSelectable {

    override val selectMe get() = this

    override val direction: SelectDirection? = null

    override val isRelationships = false

    // TODO maybe: get based on keys?
//    operator fun get(vararg vals:String):List<String> {
//        println(vals.toList())
//    }

    override fun toString():String = "SELECT NODES - keys:$keys, properties:$properties, labelName:$labelName, selectFrom:${selectFrom!=null}, "

    operator fun <T: Node>invoke(label: NodeLabel<T>):Sequence<T> = context.queryNodes(this, label)

    operator fun invoke():Sequence<Node> = context.queryNodes(this)

    inline fun forEach(block: (Node)->Unit) = invoke().forEach(block)

    override fun selectKeys(): Sequence<String> = context.selectNodeKeys(this)

    fun indexOfFirst(key:String): Int = selectKeys().indexOfFirst {it==key}

    fun contains(key: String): Boolean = this.indexOfFirst(key) > -1

    fun <T: Node>first(label: NodeLabel<T>): T? = this(label).firstOrNull()

    val first: Node? get() = this().firstOrNull()

    // TODO: yay, caching! Maybe make use of something like this?
//    val <T:Node>cachedItems: List<T> by lazy { this().toList() }
//
//    private var _cachedFirst: T? = null
//
//    var cachedFirst: T? get() {
//        _cachedFirst?.let { return it }
//        _cachedFirst = first
//        return _cachedFirst
//    }
//        set(value) {
//            _cachedFirst = value
//        }
}

// ===========================================================================================================

open class SelectRelationshipsOld(
    override val keys: List<String>? = null,
    override val properties: Map<String, Any?>? = null,
    override val labelName: String? = null,
    override val direction: SelectDirection? = null,
    override var selectFrom: SelectInterface? = null,
    override var context: Context = LocalContext // TODO maybe: explicitly pass context along when selecting?
): SelectInterface, RelationshipSelectable {

    override val selectMe get() = this

    override val isRelationships = true

    override fun toString():String = "SELECT RELATIONSHIPS - keys:$keys, properties:$properties, labelName:$labelName, direction:$direction, selectFrom:${selectFrom!=null}"

    operator fun <T: Relationship>invoke(label: RelationshipLabelInterface<T>):Sequence<T> = context.queryRelationships(this, label)

    override fun selectKeys(): Sequence<String> = context.selectRelationshipKeys(this)

    // TODO: yay, caching! Maybe make use of this?
//    override val cachedItems: List<Relationship> by lazy { asSequence().toList() }

}


//// ===========================================================================================================
// TODO: bring back if needed ...
//open class SelectEmpty:SelectNodes(keys=listOf<String>())

// ===========================================================================================================
