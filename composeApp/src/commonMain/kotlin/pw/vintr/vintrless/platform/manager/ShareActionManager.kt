package pw.vintr.vintrless.platform.manager

expect object ShareActionManager {

    val canOpenActionSheet: Boolean

    fun shareText(text: String)
}