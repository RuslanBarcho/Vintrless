package pw.vintr.vintrless.domain.profile.interactor

import pw.vintr.vintrless.domain.base.BaseInteractor
import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.profile.useCase.decodeUrl.DecodeShadowsocksUrlUseCase
import pw.vintr.vintrless.domain.profile.useCase.decodeUrl.DecodeSocksUrlUseCase
import pw.vintr.vintrless.domain.profile.useCase.decodeUrl.DecodeVlessUrlUseCase
import pw.vintr.vintrless.domain.profile.useCase.decodeUrl.DecodeVmessUrlUseCase
import pw.vintr.vintrless.domain.profile.useCase.encodeUrl.EncodeShadowsocksUrlUseCase
import pw.vintr.vintrless.domain.profile.useCase.encodeUrl.EncodeSocksUrlUseCase
import pw.vintr.vintrless.domain.profile.useCase.encodeUrl.EncodeVlessUrlUseCase
import pw.vintr.vintrless.domain.profile.useCase.encodeUrl.EncodeVmessUrlUseCase
import pw.vintr.vintrless.domain.v2ray.model.ProtocolType

class ProfileUrlInteractor : BaseInteractor() {

    fun encodeProfileUrl(profileData: ProfileData): String {
        return when (profileData.type) {
            ProtocolType.VLESS -> {
                EncodeVlessUrlUseCase(profileData)
            }
            ProtocolType.VMESS -> {
                EncodeVmessUrlUseCase(profileData)
            }
            ProtocolType.SHADOWSOCKS -> {
                EncodeShadowsocksUrlUseCase(profileData)
            }
            ProtocolType.SOCKS -> {
                EncodeSocksUrlUseCase(profileData)
            }
            else -> {
                EncodeVlessUrlUseCase(profileData)
            }
        }
    }

    fun decodeProfileUrl(urlString: String): ProfileData {
        return when {
            urlString.startsWith(ProtocolType.VLESS.protocolScheme) -> {
                DecodeVlessUrlUseCase(urlString)
            }
            urlString.startsWith(ProtocolType.VMESS.protocolScheme) -> {
                DecodeVmessUrlUseCase(urlString)
            }
            urlString.startsWith(ProtocolType.SHADOWSOCKS.protocolScheme) -> {
                DecodeShadowsocksUrlUseCase(urlString)
            }
            urlString.startsWith(ProtocolType.SOCKS.protocolScheme) -> {
                DecodeSocksUrlUseCase(urlString)
            }
            else -> {
                throw Exception("Illegal protocol scheme")
            }
        }
    }
}
