package pw.vintr.vintrless.tools.extensions

val String.Companion.Empty: String
    get() = ""

val String.Companion.Space: String
    get() = " "

val String.Companion.Comma: String
    get() = ","

val String.Companion.Dash: String
    get() = "–"

fun String.isBoolean(): Boolean {
    return this == "true" || this == "false"
}

fun String?.toBoolean(): Boolean {
    return this == "true"
}
