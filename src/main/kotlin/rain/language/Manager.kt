package rain.language

import rain.language.interfaces.*
import rain.patterns.interfaces.*
import rain.patterns.nodes.BasicPattern


open class Manager: ManagerInterface {
    final override var properties: MutableMap<String, Any?> = mutableMapOf()

    private var myNode: LanguageNode? = null

    override var deferredBlock: ((Pattern)->Unit) = { p-> }

    override val node get() = myNode

    final override val pattern get() =
        myPattern ?: makePattern(null, null)

    private var myPattern: Pattern? = null

    override fun makePattern(historyPattern: Pattern?, historyDimension: PatternDimension?) =
        node?.let { n-> patternFactory(n, historyPattern, historyDimension).also { myPattern=it } }

    override fun use(properties: MutableMap<String, Any?>) {this.properties = properties}
    override fun use(node: LanguageNode) {myNode = node;  use(node.properties)}
    override fun use(pattern: Pattern) {myPattern=pattern; use(pattern.node)}

    override var patternFactory: (LanguageNode, Pattern?, PatternDimension?)-> Pattern = { n, p, d-> BasicPattern(n,p,d) }

    override var extendDimension = PatternDimension.CHILDREN

    final override fun updatePatternFactory(factory: (LanguageNode, Pattern?, PatternDimension?)-> Pattern) {
        patternFactory = factory
    }

}
