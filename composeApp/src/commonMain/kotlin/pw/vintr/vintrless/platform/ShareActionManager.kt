package pw.vintr.vintrless.platform

expect object ShareActionManager {

    val canOpenActionSheet: Boolean

    fun shareText(text: String)
}
