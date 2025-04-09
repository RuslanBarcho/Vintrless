package pw.vintr.vintrless.domain.profile.interactor

import pw.vintr.vintrless.domain.base.BaseInteractor
import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.profile.useCase.decodeUrl.DecodeVlessUrlUseCase
import pw.vintr.vintrless.domain.profile.useCase.decodeUrl.DecodeVmessUrlUseCase
import pw.vintr.vintrless.domain.v2ray.model.ProtocolType

class ProfileUrlInteractor : BaseInteractor() {

    fun decodeProfileUrl(urlString: String): ProfileData {
        return when {
            urlString.startsWith(ProtocolType.VLESS.protocolScheme) -> {
                DecodeVlessUrlUseCase(urlString)
            }
            urlString.startsWith(ProtocolType.VMESS.protocolScheme) -> {
                DecodeVmessUrlUseCase(urlString)
            }
            else -> {
                throw Exception("Illegal protocol scheme")
            }
        }
    }
}
