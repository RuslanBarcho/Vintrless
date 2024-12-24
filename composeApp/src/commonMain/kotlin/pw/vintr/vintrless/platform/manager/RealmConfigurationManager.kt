package pw.vintr.vintrless.platform.manager

import io.realm.kotlin.RealmConfiguration

expect object RealmConfigurationManager {

    fun RealmConfiguration.Builder.applyPlatformConfiguration() : RealmConfiguration.Builder
}