package pw.vintr.vintrless.domain.userApplications.model

data class SystemProcess(
    val appName: String,
    val processName: String,
) {

    fun toApplication() = UserApplication(
        name = appName,
        processName = processName,
        executablePath = null,
    )
}
