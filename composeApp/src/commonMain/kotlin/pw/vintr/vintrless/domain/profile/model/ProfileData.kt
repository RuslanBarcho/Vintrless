package pw.vintr.vintrless.domain.profile.model

import io.realm.kotlin.ext.toRealmDictionary
import kotlinx.serialization.Serializable
import pw.vintr.vintrless.data.profile.model.ProfileDataCacheObject
import pw.vintr.vintrless.domain.profile.model.ProfileForm.Vless.unpackDefault
import pw.vintr.vintrless.domain.v2ray.model.ProtocolType
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class ProfileData @OptIn(ExperimentalUuidApi::class) constructor(
    val id: String = Uuid.random().toString(),
    val type: ProtocolType,
    val data: Map<String, String?> = mapOf()
) {

    companion object {
        fun fromCacheObject(cacheObject: ProfileDataCacheObject) = ProfileData(
            id = cacheObject.id,
            type = ProtocolType.getByCode(cacheObject.typeCode),
            data = cacheObject.data.toMap(),
        )
    }

    val name: String get() = getField(ProfileField.Name).orEmpty()

    val ip: String get() = getField(ProfileField.IP).orEmpty()

    fun setField(field: ProfileField, value: String?): ProfileData {
        return copy(
            data = data.toMutableMap().also { mutableData ->
                // Get current value and compare
                val currentValue = mutableData[field.key]
                if (value == currentValue) {
                    return@also
                }

                // Remove previous subfields values
                field.subfieldsByValue[currentValue]?.let { subfields ->
                    subfields.forEach { subfield ->
                        mutableData.remove(subfield.key)
                    }
                }

                // Add new subfield default values
                field.subfieldsByValue[value]
                    ?.unpackDefault()
                    ?.forEach { unpackedSubfield ->
                        val initialValue = unpackedSubfield.initialValue

                        if (initialValue != null) {
                            mutableData[unpackedSubfield.key] = initialValue
                        }
                    }

                // Set value
                mutableData[field.key] = value
            }
        )
    }

    fun getField(field: ProfileField): String? = data[field.key]

    fun toCacheObject() = ProfileDataCacheObject(
        id = id,
        typeCode = type.code,
        data = data.toRealmDictionary(),
    )
}
