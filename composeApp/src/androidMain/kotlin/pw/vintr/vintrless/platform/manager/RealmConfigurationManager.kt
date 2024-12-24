package pw.vintr.vintrless.platform.manager

import io.realm.kotlin.RealmConfiguration

actual object RealmConfigurationManager {

    actual fun RealmConfiguration.Builder.applyPlatformConfiguration() = this
}