package rain.language

import rain.patterns.Pattern

// TODO maybe: an interface for DefaultingField to help organize everything

abstract class AttachedField<T:Any, SN:Node, CN:Node> {

    abstract val field: Field<T, SN, CN>

    abstract val pattern: Pattern<CN>?

    protected var connectedNode: CN? = null

    // TODO maybe:
//    protected abstract val selfNode: SN

    protected abstract val defaultNode: Node?

    open val node: Node? get() = connectedNode ?: defaultNode

    fun connect(reset:Boolean=false) {
        pattern?.let {
            if (reset || connectedNode == null) connectedNode = it().firstOrNull()
        }
    }

    fun connect(node:CN?) {
        pattern?.let {
            it.clear()
            node?.let { n-> it.extend(n) }
            connectedNode = node
        }
    }

    fun connect(key:String) {
        connect(field.connectedLabel.get(key))
    }

    abstract operator fun invoke(): T?

    abstract operator fun invoke(value:T?): T?

}
// =================================

open class AttachedLocalValueField<T:Any, N: Node>(
    override val field: LocalValueField<T, N>,
    override val node:N,
): AttachedField<T, N, N>() {

    override val defaultNode = null
    override val pattern = null

    override operator fun invoke(): T? = node.properties[this.field.name] as T?

    override operator fun invoke(value:T?): T? {
        node.properties[this.field.name] = value
        return value
    }
}

open class AttachedDefaultingLocalValueField<T:Any, N: Node>(
    override val field: DefaultingLocalValueField<T, N>,
    node:N,
): AttachedLocalValueField<T,N>(field, node) {

    override operator fun invoke(): T = super.invoke() ?: this.field.default

    override operator fun invoke(value:T?): T {
        super.invoke(value)
        return value ?: this.field.default
    }
}

// =================================
// TODO: restrict pattern to RelatesPattern so that property
//  on the relationship can determine connectFieldName as opposed to var below

open class AttachedValueField<T:Any, SN: Node, CN:Node>(
    override val field: ValueField<T, SN, CN>,
    override val pattern: Pattern<CN>,
    var connectFieldName: String? = null, // TODO: WARNING - this is NOT saved in the data, but it SHOULD BE (see above)
): AttachedField<T, SN, CN>() {


    override val defaultNode get() = if (field.defaultToSelf) pattern.source else null

    fun connect(node:CN?, connectFieldName: String?) {
        connect(node)
        this.connectFieldName = connectFieldName
    }

    override operator fun invoke(): T? =
        node?.attachedFields?.get(connectFieldName ?: this.field.name)?.invoke() as T?

    override operator fun invoke(value:T?): T? {
        node?.properties?.set(this.field.name, value)
        return value
    }
}


class AttachedDefaultingValueField<T:Any, SN: Node, CN:Node>(
    override val field: DefaultingValueField<T, SN, CN>,
    pattern: Pattern<CN>,
    connectFieldName: String? = null,
): AttachedValueField<T, SN, CN>(field, pattern, connectFieldName) {

    override operator fun invoke(): T = super.invoke() ?: this.field.default

    override operator fun invoke(value:T?): T {
        super.invoke(value)
        return value ?: this.field.default
    }

}


// =================================

open class AttachedNodeField<T:Node, SN: Node>(
    override val field: NodeField<T, SN>,
    override val pattern: Pattern<T>,
): AttachedField<T, SN, T>() {

    override val defaultNode: T? = null

    override val node: T? get() = connectedNode

    override operator fun invoke(): T? = connectedNode

    override operator fun invoke(value:T?): T? {
        connect(value)
        return value
    }

}

class AttachedDefaultingNodeField<T:Node, SN: Node>(
    override val field: DefaultingNodeField<T, SN>,
    pattern: Pattern<T>,
): AttachedNodeField<T, SN>(field, pattern) {

    override val defaultNode: T get() = this.field.default

    override val node: T get() = connectedNode ?: defaultNode

    override operator fun invoke(): T = node

    override operator fun invoke(value:T?): T {
        super.invoke(value)
        return value ?: this.field.default
    }

}
