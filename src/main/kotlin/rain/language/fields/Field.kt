package rain.language.fields

import rain.language.*
import rain.patterns.*
import kotlin.reflect.KProperty

// TODO: able to implement Animatable seamlessly here?
open class Field<T:Any?>(
    val name: String,
    val default: T,
    open val cascade: Boolean = true // TODO: reconsider cascade defaults (after playing with this with solves)
) {

    open val isNode: Boolean = false

    open inner class Attached(
        val node: Node
    ) {
        val field = this@Field

        var default: T = field.default

        open val isLocal: Boolean = true

        open var value: T? = null // note that this duplicates/caches the value in the properties

        open operator fun getValue(thisRef: Any?, property: KProperty<*>): T = this.value ?: default

        open operator fun setValue(thisRef: Any?, property: KProperty<*>, value:T) {this.value = value}

        // TODO: combine with retrieveFromNode?
        fun connect(reset:Boolean=false) {
            // implemented here as an empty fun in order to be able to iterate over all
            // fields and call this (even if it does nothing)
        }

        open fun resetValue() { value = null }

        open fun resetDefault() { default = field.default }

        open fun store() { node.properties[field.name] = this.value }

        open fun retrieve() { this.value = node.properties[field.name] as T }

    }

    open fun attach(
        node: Node,
        // previous:Pattern<*>?=null // NOTE: could consider this, for now, KISS
    ): Attached = Attached(node)
 }

//// ======================================================================
//
fun <T:Any?> field(name: String, default: T? = null, cascade: Boolean = true) =
    Field(name, default, cascade)

fun <T:Any> field(name: String, default: T, cascade: Boolean = true) =
    Field(name, default, cascade)

fun <T:Any?> field(
    name: String,
    relationshipLabel: RelationshipLabel,
    default: T? = null,
    cascade: Boolean = true,
    defaultToSelf: Boolean=true
) =
    FieldConnectingValue(
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
    FieldConnectingValue(
        name,
        {s, p-> RelatesPattern(s, p, relationshipLabel)},
        default,
        cascade,
        defaultToSelf
    )

