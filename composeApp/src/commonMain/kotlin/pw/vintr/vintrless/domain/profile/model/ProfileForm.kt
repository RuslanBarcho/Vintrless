package pw.vintr.vintrless.domain.profile.model

import kotlinx.serialization.Serializable
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.profile_group_server
import vintrless.composeapp.generated.resources.profile_group_profile
import vintrless.composeapp.generated.resources.profile_group_network
import vintrless.composeapp.generated.resources.profile_group_camouflage

@Serializable
sealed class ProfileForm {

    companion object {
        val allForms: List<ProfileForm> = listOf(Vless)

        fun getByType(type: ProfileType): ProfileForm {
            return when (type) {
                ProfileType.VLESS -> Vless
            }
        }
    }

    /**
     * Profile type name
     */
    abstract val type: ProfileType

    /**
     * Available fields for profile type
     */
    abstract val fieldGroups: List<ProfileFieldGroup>

    fun getDefaultData(): Map<String, String> {
        val data = mutableMapOf<String, String>()

        fieldGroups.flatMap { group ->
            group.fields.unpackDefault()
        }.forEach { field ->
            val initialValue = field.initialValue

            if (initialValue != null) {
                data[field.key] = initialValue
            }
        }

        return data
    }

    private fun List<ProfileField>.unpackDefault(): List<ProfileField> {
        return flatMap {
            val subfields = it.subfieldsByValue[it.initialValue].orEmpty()

            if (subfields.isNotEmpty()) {
                val unpackedInternal = subfields.unpackDefault()

                listOf(it, *unpackedInternal.toTypedArray())
            } else {
                listOf(it)
            }
        }
    }

    @Serializable
    data object Vless : ProfileForm() {

        override val type = ProfileType.VLESS

        override val fieldGroups = listOf(
            ProfileFieldGroup(
                titleRes = Res.string.profile_group_server,
                fields = listOf(
                    ProfileField.Name,
                    ProfileField.IP,
                    ProfileField.Port,
                )
            ),
            ProfileFieldGroup(
                titleRes = Res.string.profile_group_profile,
                fields = listOf(
                    ProfileField.UserId,
                    ProfileField.Flow,
                    ProfileField.Encryption,
                )
            ),
            ProfileFieldGroup(
                titleRes = Res.string.profile_group_network,
                fields = listOf(
                    ProfileField.TransportProtocol,
                    ProfileField.HeaderType,
                    ProfileField.HttpNode,
                    ProfileField.Path
                )
            ),
            ProfileFieldGroup(
                titleRes = Res.string.profile_group_camouflage,
                fields = listOf(ProfileField.TLS)
            )
        )
    }
}
