package rain.language

import rain.language.fields.ConnectingDefaultingNodeField
import rain.language.fields.FieldConnectingNode
import rain.language.fields.FieldConnectingValue
import rain.patterns.Pattern

// TODO maybe: an interface for DefaultingField to help organize everything








interface AttachedConnecting<NT:Node?, T:Any?>: AttachedField<T> {

    val pattern: Pattern<*>

    override val attachedNode get() = pattern.source

    var connectedNode: NT?

    //    val connectField: Field<T, CN, *>? // no need for this since we have to type cast anyway
    override val isLocal: Boolean get() = (connectedNode == null)

    // TODO: why is this (CN?)? Change to (CN)?
    fun connect(node:NT?) {
        pattern.clear()
        node?.let { n-> pattern.extend(n) }
        connectedNode = node
    }

    fun connect(key:String)

}


open class AttachedConnectingNode<T: Node>(
    override val field: FieldConnectingNode<T>,
    override val pattern: Pattern<*>,
    override var default: T? = field.default
): AttachedConnecting<T, T?> {

    override var connectedNode: T? = null

    override fun resetValue() { connectedNode = null }

    override fun connect(reset:Boolean) {
        if (reset || connectedNode == null) connectedNode = pattern(field.label).firstOrNull()
    }

    override fun connect(key:String) {
        connect(field.label.get(key))
    }

    override var value: T?
        get() = connectedNode ?: this.default
        set(value) {
            connect(value)
        }

}

class AttachedConnectingDefaultingNode<T: Node>(
    override val field: ConnectingDefaultingNodeField<T>,
    override val pattern: Pattern<*>,
    override var default: T = field.default
): AttachedConnecting<T, T> {

    override var connectedNode: T? = null

    override fun resetValue() { connectedNode = null }

    override fun connect(reset:Boolean) {
        if (reset || connectedNode == null) connectedNode = pattern(field.label).firstOrNull()
    }

    override fun connect(key:String) {
        connect(field.label.get(key))
    }

    override var value: T
        get() = connectedNode ?: this.default
        set(value) {
            connect(value)
        }

}


// NOTE that values of nodes are not supported
class AttachedConnectingValue<T:Any?>(
    override val field: FieldConnectingValue<T>,
    override val pattern: Pattern<*>,
    override var default: T = field.default
): AttachedConnecting<Node, T> {

    override var connectedNode: Node? = null

    var connectFieldName: String? get() = attachedNode.properties[this.field.name + ":connectField"] as String?
        set(value) {
            attachedNode.properties[this.field.name + ":connectField"] = value
        }

    val connectedField: AttachedField<Any?>? get() = connectFieldName?.let { fn-> connectedNode?.attachedFields?.get(fn)  }

    override fun resetValue() { connectedField?.resetValue() }

    override fun connect(reset:Boolean) {
        if (reset || connectedNode == null) connectedNode = pattern().firstOrNull()
    }

    override fun connect(key:String) {
        connect(attachedNode.context.nodeFrom(key))
    }

    fun connect(node:Node?, connectFieldName:String?) {
        super.connect(node)
        this.connectFieldName = connectFieldName
    }

    // if making a point of connecting... assume that we want a connection and default
    // connectFieldName to the name of the field
    override fun connect(node:Node?) {
        connect(node, this.field.name)
    }

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


