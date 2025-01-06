package pw.vintr.vintrless.domain.userApplications.model

/**
 * @name - app name
 * @processName - process or package name, using for filtering
 * @executablePath - path to application's executable file; Desktop-only field
 */
data class UserApplication(
    val name: String,
    val processName: String,
    val executablePath: String?,
)
