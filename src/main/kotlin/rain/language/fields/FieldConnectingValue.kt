package rain.language.fields

import rain.language.AttachedConnecting
import rain.language.AttachedConnectingValue
import rain.language.Node
import rain.language.NodeLabel
import rain.patterns.Pattern

open class FieldConnectingFieldValue<T:Any?>(
    name: String,
    val patternFactory: (source: Node)-> Pattern<*>,
//    val connectingField: Field<T>, // TODO: is this flexible enough
    default: T,
    cascade: Boolean = true,
    val defaultToSelf:Boolean = true
): Field<T>(name, default, cascade) {

    override val isNode: Boolean = true

    inner class Attached(
        node: Node,
        val pattern: Pattern<*>,
    ): Field<T>.Attached(node) {

        var connectedNode: Node? = null

        // TODO: is this a better implementation for connectingField
//        var connectFieldName: String?
//            get() =
//                node.properties[this.field.name + ":connectField"] as String?
//            set(value) {
//                node.properties[this.field.name + ":connectField"] = value
//            }
//
//        val connectingField: Field<Any?>.Attached? get() = connectFieldName?.let { fn-> connectedNode?.attachedFields?.get(fn)  }

        override fun resetValue() { connectedField?.resetValue() }

//        override fun connect(reset:Boolean) {
//            if (reset || connectedNode == null) connectedNode = pattern().firstOrNull()
//        }

        override fun retrieve() {
            connectedNode = pattern().firstOrNull()
            connectedNode?.let {cn-> value = cn[connectingField]; return }
            super.retrieve() // if no connectedNode, then calls super to set value based on properties
        }

        override fun store()  {
            connectedNode?.let {cn-> cn[connectingField] = value; return }
            super.store() // if no connectedNode, then calls super store value in properties
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

//        override var value: T?
//            get() =
//                (connectedField?.value as T? ?: if (this@FieldConnectingValue.defaultToSelf) node[this@FieldConnectingValue] else null )
//            set(value) {
//                connectedField?.let {
//                    it.value = value
//                    return
//                }
//                if (this@FieldConnectingValue.defaultToSelf) {
//                    node[this@FieldConnectingValue] = value
//                }
//                println("WARNING: attempted to set field '${this.field.name}' value on connected node, but node not connected, and not defaulting to self.")
//            }

    }


    override fun attach(node: Node) = Attached(node, patternFactory(node))

}