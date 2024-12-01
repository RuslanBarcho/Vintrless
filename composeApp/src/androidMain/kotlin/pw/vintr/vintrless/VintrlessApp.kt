package pw.vintr.vintrless

import android.app.Application
import pw.vintr.vintrless.tools.AppContext

class VintrlessApp : Application() {

    override fun onCreate() {
        super.onCreate()

        AppContext.setUp(this)
    }
}
