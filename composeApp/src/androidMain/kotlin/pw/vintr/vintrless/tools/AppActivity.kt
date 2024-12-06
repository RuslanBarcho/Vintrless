package pw.vintr.vintrless.tools

import android.app.Activity

internal object AppActivity {

    lateinit var activityProvider: () -> Activity

    fun setUp(provider: () -> Activity) {
        activityProvider = provider
    }

    fun get(): Activity {
        if (AppActivity::activityProvider.isInitialized.not()) {
            throw Exception("Activity provider isn't initialized")
        }
        return activityProvider()
    }
}
