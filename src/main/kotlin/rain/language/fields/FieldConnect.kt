package rain.language.fields

import rain.language.Node
import rain.patterns.Pattern
import kotlin.reflect.KProperty

// TODO: review naming and logic for confusion
open class FieldConnect<T:Any?>(
    name: String,
    val patternFactory: (source: Node)-> Pattern<*>,
    val fieldNode: FieldNode<*>? = null,
    val fieldNodeField: Field<T>? = null,
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

        // TODO: is this a better implementation for connectingField
        var connectFieldName: String?
            get() =
                node.properties[this.field.name + ":connectField"] as String?
            set(value) {
                node.properties[this.field.name + ":connectField"] = value
            }

        var attachedField: Field<T?>.Attached? = null

        override fun resetValue() { attachedField?.resetValue() } // TODO: is this OK?

        override fun retrieve() {
            pattern().firstOrNull()?.let {cn ->
                attachedField = connectFieldName?.let { fn-> cn.attachedField(fn) }
            }
            if (attachedField == null) this@FieldConnect.fieldNode?.let { fcn ->
                node[fcn]?.let { cn ->
                    (this@FieldConnect.fieldNodeField?.name ?: connectFieldName)?.let { fn ->
                        attachedField = cn.attachedField(fn)
                    }
                }
            }

            // if no attachedField and defaulting to self,
            // then calls super to set local value based on properties
            if (this@FieldConnect.defaultToSelf) super.retrieve()

        }

        override fun store()  {
            // if no attachedField and defaulting to self,
            // then calls super to set local value based on properties
            if (attachedField==null && this@FieldConnect.defaultToSelf) super.store()
        }

        fun connect(node:Node?, connectFieldName:String?) {
            pattern.clear()
            node?.let { pattern.extend(it) }
            retrieve()
        }

        override operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
            attachedField?.value ?: this.value ?: default

        override operator fun setValue(thisRef: Any?, property: KProperty<*>, value:T) {
            attachedField?.let {
                it.value = value
                return
            }
            if (this@FieldConnect.defaultToSelf) {
                this.value = value
                return
            }
            println("WARNING: attempted to set field '${this.field.name}' value on connected node, but node not connected, and not defaulting to self.")
        }

    }


    override fun attach(node: Node) = Attached(node, patternFactory(node))

}