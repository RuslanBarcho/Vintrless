package pw.vintr.vintrless.domain.profile.model

import kotlinx.serialization.Serializable
import pw.vintr.vintrless.domain.v2ray.model.ProtocolType
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.profile_group_server
import vintrless.composeapp.generated.resources.profile_group_profile
import vintrless.composeapp.generated.resources.profile_group_network
import vintrless.composeapp.generated.resources.profile_group_camouflage

@Serializable
sealed class ProfileForm {

    companion object {
        val allForms: List<ProfileForm> = listOf(Vless, Vmess, Shadowsocks, Socks, Http, Trojan, WireGuard)

        fun getByType(type: ProtocolType): ProfileForm {
            return when (type) {
                ProtocolType.VLESS -> Vless
                ProtocolType.VMESS -> Vmess
                ProtocolType.SHADOWSOCKS -> Shadowsocks
                ProtocolType.SOCKS -> Socks
                ProtocolType.HTTP -> Http
                ProtocolType.TROJAN -> Trojan
                ProtocolType.WIREGUARD -> WireGuard
                ProtocolType.HYSTERIA2 -> Vless
            }
        }
    }

    /**
     * Profile type name
     */
    abstract val type: ProtocolType

    /**
     * Available fields for profile type
     */
    abstract val fieldGroups: List<ProfileFieldGroup>

    fun getAllFields() = fieldGroups.flatMap { group ->
        group.fields.unpackAll()
    }

    private fun List<ProfileField>.unpackAll(): List<ProfileField> {
        return flatMap {
            val subfields = it.subfieldsByValue.values.flatten().distinct()

            if (subfields.isNotEmpty()) {
                val unpackedInternal = subfields.unpackAll()

                listOf(it, *unpackedInternal.toTypedArray())
            } else {
                listOf(it)
            }
        }
    }

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

    fun List<ProfileField>.unpackDefault(): List<ProfileField> {
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

        override val type = ProtocolType.VLESS

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
                )
            ),
            ProfileFieldGroup(
                titleRes = Res.string.profile_group_camouflage,
                fields = listOf(ProfileField.TLS)
            )
        )
    }

    @Serializable
    data object Vmess : ProfileForm() {

        override val type = ProtocolType.VMESS

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
                    ProfileField.VmessSecurity,
                )
            ),
            ProfileFieldGroup(
                titleRes = Res.string.profile_group_network,
                fields = listOf(
                    ProfileField.TransportProtocol,
                )
            ),
            ProfileFieldGroup(
                titleRes = Res.string.profile_group_camouflage,
                fields = listOf(ProfileField.TLS)
            )
        )
    }

    @Serializable
    data object Shadowsocks : ProfileForm() {

        override val type = ProtocolType.SHADOWSOCKS

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
                    ProfileField.Password,
                    ProfileField.SSocksSecurity,
                )
            ),
            ProfileFieldGroup(
                titleRes = Res.string.profile_group_network,
                fields = listOf(
                    ProfileField.TransportProtocol,
                )
            ),
            ProfileFieldGroup(
                titleRes = Res.string.profile_group_camouflage,
                fields = listOf(ProfileField.TLS)
            )
        )
    }

    @Serializable
    data object Socks : ProfileForm() {

        override val type = ProtocolType.SOCKS

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
                    ProfileField.UserName,
                    ProfileField.Password,
                )
            ),
        )
    }

    @Serializable
    data object Http : ProfileForm() {

        override val type = ProtocolType.HTTP

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
                    ProfileField.UserName,
                    ProfileField.Password,
                )
            ),
        )
    }

    @Serializable
    data object Trojan : ProfileForm() {

        override val type = ProtocolType.TROJAN

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
                    ProfileField.Password,
                )
            ),
            ProfileFieldGroup(
                titleRes = Res.string.profile_group_network,
                fields = listOf(
                    ProfileField.TransportProtocol,
                )
            ),
            ProfileFieldGroup(
                titleRes = Res.string.profile_group_camouflage,
                fields = listOf(ProfileField.TLS)
            )
        )
    }

    @Serializable
    data object WireGuard : ProfileForm() {

        override val type = ProtocolType.WIREGUARD

        override val fieldGroups = listOf(
            ProfileFieldGroup(
                titleRes = Res.string.profile_group_server,
                fields = listOf(
                    ProfileField.Name,
                    ProfileField.IP,
                    ProfileField.Port,
                    ProfileField.ServerPrivateKey,
                    ProfileField.ServerPublicKey,
                    ProfileField.ServerAdditionalKey,
                    ProfileField.Reserved,
                    ProfileField.LocalAddress,
                    ProfileField.MTU,
                ),
            ),
        )
    }
}
