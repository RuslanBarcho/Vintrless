package pw.vintr.vintrless.domain.userApplications.model.common

interface IDeviceApplication {

    val uuid: String

    val appName: String

    val processName: String

    val savedByUser: Boolean
}
