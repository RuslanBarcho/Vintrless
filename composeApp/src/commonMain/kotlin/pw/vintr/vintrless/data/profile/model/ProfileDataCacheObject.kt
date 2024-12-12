package pw.vintr.vintrless.data.profile.model

import io.realm.kotlin.ext.realmDictionaryOf
import io.realm.kotlin.types.RealmDictionary
import io.realm.kotlin.types.RealmObject
import pw.vintr.vintrless.tools.extensions.Empty

class ProfileDataCacheObject() : RealmObject {

    var id: String = String.Empty

    var typeCode: String = String.Empty

    var data: RealmDictionary<String?> = realmDictionaryOf()

    constructor(
        id: String = String.Empty,
        typeCode: String = String.Empty,
        data: RealmDictionary<String?> = realmDictionaryOf(),
    ) : this() {
        this.id = id
        this.typeCode = typeCode
        this.data = data
    }
}
