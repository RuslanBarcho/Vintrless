package pw.vintr.vintrless.domain.profile.useCase.encodeUrl.base

import com.eygraber.uri.UriCodec
import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.profile.model.ProfileField
import pw.vintr.vintrless.domain.profile.model.ProfileForm
import pw.vintr.vintrless.tools.extensions.Empty
import pw.vintr.vintrless.tools.network.IPTools

abstract class BaseEncodeProfileUrlUseCase {

    abstract operator fun invoke(profile: ProfileData): String

    protected open fun encode(profile: ProfileData, userInfo: String): String {
        val form = ProfileForm.getByType(profile.type)
        val fields = form.getAllFields()

        val queryList = fields
            .filter { it.queryKey.isNotEmpty() }
            .mapNotNull { field ->
                profile.data[field.key]?.let { field.queryKey to it }
            }

        val query = if (queryList.isNotEmpty()) {
            "?" + queryList.joinToString(
                separator = "&",
                transform = { it.first + "=" + UriCodec.encode(it.second) }
            )
        } else {
            String.Empty
        }

        // Build base url part
        val ip = IPTools.getIpv6Address(profile.ip)
        val port = profile.data[ProfileField.Port.key].orEmpty()

        val url = "$userInfo@$ip:$port"

        // Build and return url with query
        val name = UriCodec.encode(profile.name)

        return "${form.type.protocolScheme}$url$query#$name"
    }
}
