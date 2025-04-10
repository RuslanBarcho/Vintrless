package pw.vintr.vintrless.domain.profile.interactor

import pw.vintr.vintrless.domain.base.BaseInteractor
import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.profile.useCase.decodeUrl.*
import pw.vintr.vintrless.domain.profile.useCase.encodeUrl.*
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
            ProtocolType.HTTP -> {
                EncodeHttpUrlUseCase(profileData)
            }
            ProtocolType.TROJAN -> {
                EncodeTrojanUrlUseCase(profileData)
            }
            ProtocolType.WIREGUARD -> {
                EncodeWireguardUrlUseCase(profileData)
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
            urlString.startsWith(ProtocolType.HTTP.protocolScheme) -> {
                DecodeHttpUrlUseCase(urlString)
            }
            urlString.startsWith(ProtocolType.TROJAN.protocolScheme) -> {
                DecodeTrojanUrlUseCase(urlString)
            }
            urlString.startsWith(ProtocolType.WIREGUARD.protocolScheme) -> {
                DecodeWireguardUrlUseCase(urlString)
            }
            else -> {
                throw Exception("Illegal protocol scheme")
            }
        }
    }
}
