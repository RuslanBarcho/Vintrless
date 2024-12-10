package pw.vintr.vintrless.tools

internal object PathProvider {

    private val applicationPath: String = System.getProperty("user.dir")

    val resourcesPath: String = System.getProperty("compose.application.resources.dir")

    val dataStoreFilePath = "$applicationPath/preferences/vintrless.preferences_pb"

    val databaseDirectoryPath = "$applicationPath/db"
}
