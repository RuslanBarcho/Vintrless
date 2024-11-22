package pw.vintr.vintrless.domain.profile.model

import kotlinx.serialization.Serializable
import pw.vintr.vintrless.data.profile.model.ProfileDataStorageObject
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class ProfileData @OptIn(ExperimentalUuidApi::class) constructor(
    val id: String = Uuid.random().toString(),
    val type: ProfileType,
    val data: Map<String, String?> = mapOf()
) {

    companion object {
        fun fromStorageObject(cacheObject: ProfileDataStorageObject) = ProfileData(
            id = cacheObject.id,
            type = ProfileType.getByCode(cacheObject.typeCode),
            data = cacheObject.data,
        )
    }

    fun setField(field: ProfileField, value: String?): ProfileData {
        return copy(
            data = data.toMutableMap().also { mutableData ->
                val currentValue = mutableData[field.key]
                if (value == currentValue) {
                    return@also
                }

                field.subfieldsByValue[currentValue]?.let { subfields ->
                    subfields.forEach { subfield ->
                        mutableData.remove(subfield.key)
                    }
                }

                mutableData[field.key] = value
            }
        )
    }

    fun getField(field: ProfileField): String? = data[field.key]

    fun toStorageObject() = ProfileDataStorageObject(
        id = id,
        typeCode = type.code,
        data = data,
    )
}
