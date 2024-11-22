package pw.vintr.vintrless.data.profile.model

import kotlinx.serialization.Serializable
import pw.vintr.vintrless.data.storage.StorageObject

@Serializable
data class ProfileDataStorageObject(
    override val id: String,
    val typeCode: String,
    val data: Map<String, String?>,
) : StorageObject
