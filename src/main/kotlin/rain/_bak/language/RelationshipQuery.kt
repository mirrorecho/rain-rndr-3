package rain._bak.language

//import rain.language._bak2.SelectDirection
//import rain.language.Filter
//import rain.language.Query
//import rain.language.Queryable
//import rain.patterns.Pattern
//
//// TODO: is this even needed?
//abstract class RelationshipQuery: Query, Queryable {
//
//    override val selectKeys: Array<out String> = arrayOf()
//    override val labelName: String? = null
//    override val predicate: Filter? = null
//    override var fromQuery: Query? = null
//    override var context: Context = LocalContext // TODO maybe: explicitly pass context along when selecting?
//
//    override val queryMe get() = this
//
//    override val direction: SelectDirection = SelectDirection.RIGHT
//    override val isRelationships: Boolean = true
//
//    override operator fun invoke(): Sequence<Node> = context.queryNodes(this)
//
//    // TODO: breaking separation of concern with interfaces here... maybe, create a PatternInterface?
//    fun asPatterns(): Sequence<Pattern> = this().map { it.makePattern() }
//
//    inline fun forEach(block: (Node)->Unit) = this().forEach(block)
//
//    override fun asKeys(): Sequence<String> = context.selectRelationshipKeys(this)
//
//
//}