package pw.vintr.vintrless.platform

import io.realm.kotlin.RealmConfiguration
import pw.vintr.vintrless.tools.PathProvider

actual object RealmConfigurationManager {

    actual fun RealmConfiguration.Builder.applyPlatformConfiguration() =
        directory(PathProvider.databaseDirectoryPath)
}
