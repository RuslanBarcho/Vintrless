package pw.vintr.vintrless.platform

import io.realm.kotlin.RealmConfiguration

actual object RealmConfigurationManager {

    actual fun RealmConfiguration.Builder.applyPlatformConfiguration() = this
}
