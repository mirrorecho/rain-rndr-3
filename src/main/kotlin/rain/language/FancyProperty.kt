package rain.language
import rain.language.interfaces.Context
import rain.utils.*

class FancyProperty<T>(
    val name: String,
    val value: T,
    val context: Context,
    val universalName: String = autoKey(),

    ) {
    val graphValue: String get() = ":FANCY:$universalName"

}