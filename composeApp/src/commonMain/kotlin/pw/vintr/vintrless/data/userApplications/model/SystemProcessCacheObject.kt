package pw.vintr.vintrless.data.userApplications.model

import io.realm.kotlin.types.RealmObject
import pw.vintr.vintrless.tools.extensions.Empty

class SystemProcessCacheObject() : RealmObject {

    var id: String = String.Empty

    var appName: String = String.Empty

    var processName: String = String.Empty

    constructor(
        id: String = String.Empty,
        appName: String = String.Empty,
        processName: String = String.Empty,
    ) : this() {
        this.id = id
        this.appName = appName
        this.processName = processName
    }
}
