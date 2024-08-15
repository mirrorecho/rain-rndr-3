package rain.language

import rain.patterns.Pattern
import rain.patterns.nodes.Machine

// TODO maybe: an interface for DefaultingField to help organize everything

interface  AttachedField<T:Any?> {

    val field: Field<T>
    val attachedNode: Node
    var default: T
    val isLocal: Boolean

    var value: T

    fun connect(reset:Boolean=false) {
        // implemented here as an empty fun in order to be able to iterate over all
        // fields and call this (even if it does nothing)
    }

    fun resetValue()

    fun resetDefault() { default = field.default }

}


class AttachedLocalValue<T:Any?>(
    override val field: Field<T>,
    override val attachedNode: Node,
    override var default: T = field.default
): AttachedField<T> {

    override val isLocal = true

    override fun resetValue() { attachedNode.properties[this.field.name] = null }

    override var value: T
        get() = attachedNode.properties.getOrDefault(this.field.name, default) as T
        set(value) {attachedNode.properties[this.field.name] = value}

}



abstract class AttachedConnecting<T:Any?>: AttachedField<T> {

    abstract val pattern: Pattern<*>

    override val attachedNode get() = pattern.source

    var connectedNode: Node? = null

    //    val connectField: Field<T, CN, *>? // no need for this since we have to type cast anyway
    override val isLocal: Boolean get() = (connectedNode == null)

    override fun connect(reset:Boolean) {
        pattern.let {
            if (reset || connectedNode == null) connectedNode = it().firstOrNull()
        }
    }

    // TODO: why is this (CN?)? Change to (CN)?
    fun connect(node:Node?) {
        pattern.clear()
        node?.let { n-> pattern.extend(n) }
        connectedNode = node
    }

    fun connect(key:String) {
        connect(attachedNode.context.nodeFrom(key))
    }

}


class AttachedConnectingNode<T:Node?>(
    override val field: Field<T>,
    override val pattern: Pattern<*>,
    override var default: T = field.default
): AttachedConnecting<T>() {

    override fun resetValue() { connectedNode = null }

    override var value: T
        get() = connectedNode as T? ?: default
        set(value) {
            connect(value)
        }

}

// NOTE that the value could still be a node as long as field.defaultToSelf is false
class AttachedConnectingValue<T:Any?>(
    override val field: Field<T>,
    override val pattern: Pattern<*>,
    override var default: T = field.default
): AttachedConnecting<T>() {

    val connectFieldName: String? get() = attachedNode.properties[this.field.name + ":connect"] as String?

    val connectedField: AttachedField<Any?>? get() = connectFieldName?.let { connectedNode?.attachedFields?.get(it)  }

    override fun resetValue() { connectedField?.resetValue() }

    override var value: T
        get() =
            (connectedField?.value ?: if (this.field.defaultToSelf) attachedNode.properties[this.field.name] else null ) as T? ?: default
        set(value) {
            connectedField?.let {
                it.value = value
                return
            }
            if (this.field.defaultToSelf) {
                attachedNode.properties[this.field.name] = value
            }
            println("WARNING: attempted to set field '${this.field.name}' value on connected node, but node not connected, and not defaulting to self.")
        }

}


