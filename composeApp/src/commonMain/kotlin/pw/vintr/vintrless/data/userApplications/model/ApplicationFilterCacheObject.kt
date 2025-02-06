package pw.vintr.vintrless.data.userApplications.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import pw.vintr.vintrless.tools.extensions.Empty

class ApplicationFilterCacheObject() : RealmObject {

    var id: String = String.Empty

    var filterKeys: RealmList<String> = realmListOf()

    constructor(
        id: String = String.Empty,
        filterKeys: RealmList<String> = realmListOf()
    ) : this() {
        this.id = id
        this.filterKeys = filterKeys
    }
}
