package rain.language

import rain.language.interfaces.*
import rain.patterns.interfaces.*
import rain.utils.lazyish


open class Manager(
    override val properties: MutableMap<String, Any?>,
    ): ManagerInterface {
    constructor(
        node: LanguageNode,
    ) : this(node.properties)


//    final override var properties: MutableMap<String, Any?> = mutableMapOf()

    final override val node by lazyish { myNode }
    override var pattern: Pattern? by lazyish { node?.let { Pattern(it) } }

    private var myNode: LanguageNode? = null

    override var extendDimension = DimensionLabel.CHILDREN

    override var deferredBlocks = mutableListOf<((Pattern)->Unit)>()

    // TODO: this doesn't work...!!!
    override fun manage(properties: MutableMap<String, Any?>) {this.properties = properties}
    override fun manage(node: LanguageNode) {node.manager = this; myNode = node;  manage(node.properties)}
    override fun manage(pattern: Pattern) {this.pattern=pattern; manage(pattern.node)}

}
