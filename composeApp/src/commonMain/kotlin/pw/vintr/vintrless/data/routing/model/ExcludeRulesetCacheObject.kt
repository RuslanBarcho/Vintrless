package pw.vintr.vintrless.data.routing.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import pw.vintr.vintrless.tools.extensions.Empty

class ExcludeRulesetCacheObject() : RealmObject {

    var id: String = String.Empty

    var ips: RealmList<String> = realmListOf()

    var domains: RealmList<String> = realmListOf()

    constructor(
        id: String = String.Empty,
        ips: RealmList<String> = realmListOf(),
        domains: RealmList<String> = realmListOf(),
    ) : this() {
        this.id = id
        this.ips = ips
        this.domains = domains
    }
}
