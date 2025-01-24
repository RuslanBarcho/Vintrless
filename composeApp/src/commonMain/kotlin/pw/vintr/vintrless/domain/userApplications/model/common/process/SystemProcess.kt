package pw.vintr.vintrless.domain.userApplications.model.common.process

import pw.vintr.vintrless.data.userApplications.model.SystemProcessCacheObject
import pw.vintr.vintrless.domain.userApplications.model.common.IDeviceApplication
import pw.vintr.vintrless.tools.extensions.Empty

data class SystemProcess(
    val id: String = String.Empty,
    override val appName: String,
    override val processName: String,
) : IDeviceApplication {

    override val uuid: String = id

    override val savedByUser: Boolean = true

    companion object {
        fun fromCacheObject(cacheObject: SystemProcessCacheObject): SystemProcess = SystemProcess(
            id = cacheObject.id,
            appName = cacheObject.appName,
            processName = cacheObject.processName
        )
    }

    fun toCacheObject() = SystemProcessCacheObject(
        id = id,
        appName = appName,
        processName = processName
    )
}
