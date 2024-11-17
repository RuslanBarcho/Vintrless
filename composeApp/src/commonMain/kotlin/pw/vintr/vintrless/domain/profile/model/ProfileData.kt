package pw.vintr.vintrless.domain.profile.model

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class ProfileData @OptIn(ExperimentalUuidApi::class) constructor(
    val id: String = Uuid.random().toString(),
    val type: ProfileType,
    val data: Map<String, String?> = mapOf()
) {

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
}
