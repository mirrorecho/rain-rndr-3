package rain.language.interfacing

import rain.language.Context
import rain.language.Item
import rain.language.LocalContext

abstract class Label<T: Item> {
    abstract val labelName:String
    abstract val allNames: List<String>
    val isRelationship:Boolean = false
    var context: Context = LocalContext // TODO: OK to default to LocalContext here? And should this be a val instead?
}
