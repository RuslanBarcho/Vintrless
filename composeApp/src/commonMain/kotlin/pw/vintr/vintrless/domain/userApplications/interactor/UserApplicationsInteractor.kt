package pw.vintr.vintrless.domain.userApplications.interactor

import pw.vintr.vintrless.data.userApplications.repository.UserApplicationsRepository
import pw.vintr.vintrless.domain.base.BaseInteractor
import pw.vintr.vintrless.domain.userApplications.model.common.process.SystemProcess
import pw.vintr.vintrless.domain.userApplications.model.common.application.UserApplication
import pw.vintr.vintrless.platform.manager.UserApplicationsManager

class UserApplicationsInteractor(
    private val userApplicationsRepository: UserApplicationsRepository
) : BaseInteractor() {

    suspend fun getUserApplications(): List<UserApplication> {
        return UserApplicationsManager.getUserApplications()
    }

    suspend fun getRunningProcesses(): List<SystemProcess> {
        return UserApplicationsManager.getRunningProcesses()
    }

    suspend fun saveProcess(process: SystemProcess) {
        userApplicationsRepository.saveSystemProcess(process)
    }

    suspend fun getSavedProcesses(): List<SystemProcess> {
        return userApplicationsRepository.getSavedSystemProcesses()
    }
}
