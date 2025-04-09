package pw.vintr.vintrless.domain.profile.useCase.decodeUrl.network

import pw.vintr.vintrless.domain.profile.model.ProfileField
import pw.vintr.vintrless.domain.v2ray.model.NetworkType

object DecodeNetworkUrlUseCase {

    operator fun invoke(queryParams: Map<String, String>): Map<String, String> {
        val outputFieldsMap = mutableMapOf<String, String>()

        val networkType = queryParams["type"] ?: NetworkType.TCP.type

        val headerType = queryParams["headerType"]
        val host = queryParams["host"]
        val path = queryParams["path"]

        val seed = queryParams["seed"]
        val mode = queryParams["mode"]
        val serviceName = queryParams["serviceName"]
        val authority = queryParams["authority"]
        val xhttpMode = queryParams["mode"]
        val xhttpExtra = queryParams["extra"]

        when (networkType) {
            NetworkType.TCP.type -> {
                headerType?.let { outputFieldsMap[ProfileField.TcpHeaderType.key] = it }
                host?.let { outputFieldsMap[ProfileField.TcpHost.key] = it }
                path?.let { outputFieldsMap[ProfileField.TcpPath.key] = it }
            }
            NetworkType.KCP.type -> {
                headerType?.let { outputFieldsMap[ProfileField.KcpHeaderType.key] = it }
                host?.let { outputFieldsMap[ProfileField.KcpHost.key] = it }
                seed?.let { outputFieldsMap[ProfileField.KcpSeed.key] = it }
            }
            NetworkType.WS.type -> {
                host?.let { outputFieldsMap[ProfileField.WSHost.key] = it }
                path?.let { outputFieldsMap[ProfileField.WSPath.key] = it }
            }
            NetworkType.HTTP_UPGRADE.type -> {
                host?.let { outputFieldsMap[ProfileField.HTTPUpgradeHost.key] = it }
                path?.let { outputFieldsMap[ProfileField.HTTPUpgradePath.key] = it }
            }
            NetworkType.XHTTP.type -> {
                xhttpMode?.let { outputFieldsMap[ProfileField.XHTTPMode.key] = it }
                host?.let { outputFieldsMap[ProfileField.XHTTPHost.key] = it }
                path?.let { outputFieldsMap[ProfileField.XHTTPPath.key] = it }
                xhttpExtra?.let { outputFieldsMap[ProfileField.XHTTPJsonExtra.key] = it }
            }
            NetworkType.SPLIT_HTTP.type -> {
                xhttpMode?.let { outputFieldsMap[ProfileField.XHTTPMode.key] = it }
                host?.let { outputFieldsMap[ProfileField.XHTTPHost.key] = it }
                path?.let { outputFieldsMap[ProfileField.XHTTPPath.key] = it }
                xhttpExtra?.let { outputFieldsMap[ProfileField.XHTTPJsonExtra.key] = it }
            }
            NetworkType.H2.type -> {
                host?.let { outputFieldsMap[ProfileField.H2Host.key] = it }
                path?.let { outputFieldsMap[ProfileField.H2Path.key] = it }
            }
            NetworkType.GRPC.type -> {
                mode?.let { outputFieldsMap[ProfileField.GRPCMode.key] = it }
                authority?.let { outputFieldsMap[ProfileField.GRPCAuthority.key] = it }
                serviceName?.let { outputFieldsMap[ProfileField.GRPCService.key] = it }
            }
        }

        outputFieldsMap[ProfileField.TransportProtocol.key] = networkType

        return outputFieldsMap
    }
}
