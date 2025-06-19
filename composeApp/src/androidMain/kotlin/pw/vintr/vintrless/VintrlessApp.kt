package pw.vintr.vintrless

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import pw.vintr.vintrless.tools.AppContext
import pw.vintr.vintrless.tools.modules.appModule

class VintrlessApp : Application() {

    override fun onCreate() {
        super.onCreate()

        AppContext.setUp(this)

        startKoin {
            androidContext(this@VintrlessApp)
            modules(appModule)
        }
    }
}
