package rain.language

import rain.language.interfaces.*
import rain.patterns.interfaces.*
import rain.patterns.nodes.BasicPattern


open class Manager: ManagerInterface {
    final override var properties: MutableMap<String, Any?> = mutableMapOf()

    private var myNode: LanguageNode? = null

    override var deferredBlocks = mutableListOf<((Pattern)->Unit)>()

    override val node get() = myNode

    final override val pattern get() =
        myPattern ?: makePattern(null, null)

    private var myPattern: Pattern? = null

    override fun makePattern(historyPattern: Pattern?, historyDimension: PatternDimension?) =
        node?.let { n-> patternFactory(n, historyPattern, historyDimension).also { myPattern=it } }

    override fun manage(properties: MutableMap<String, Any?>) {this.properties = properties}
    override fun manage(node: LanguageNode) {myNode = node;  manage(node.properties)}
    override fun manage(pattern: Pattern) {myPattern=pattern; manage(pattern.node)}

    override var patternFactory: (LanguageNode, Pattern?, PatternDimension?)-> Pattern = { n, p, d-> BasicPattern(n,p,d) }

    override var extendDimension = PatternDimension.CHILDREN

    final override fun updatePatternFactory(factory: (LanguageNode, Pattern?, PatternDimension?)-> Pattern) {
        patternFactory = factory
    }


}
