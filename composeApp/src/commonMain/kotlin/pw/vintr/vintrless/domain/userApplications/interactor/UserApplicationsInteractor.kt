package pw.vintr.vintrless.domain.userApplications.interactor

import pw.vintr.vintrless.data.userApplications.repository.UserApplicationsRepository
import pw.vintr.vintrless.domain.base.BaseInteractor
import pw.vintr.vintrless.domain.userApplications.model.common.process.SystemProcess
import pw.vintr.vintrless.domain.userApplications.model.common.application.UserApplication
import pw.vintr.vintrless.domain.userApplications.model.filter.ApplicationFilter
import pw.vintr.vintrless.domain.userApplications.model.filter.ApplicationFilterConfig
import pw.vintr.vintrless.domain.userApplications.model.filter.ApplicationFilterMode
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
        userApplicationsRepository.saveSystemProcess(process.toCacheObject())
    }

    suspend fun getSavedProcesses(): List<SystemProcess> {
        return userApplicationsRepository.getSavedSystemProcesses()
            .map { SystemProcess.fromCacheObject(it) }
    }

    suspend fun saveFilter(filter: ApplicationFilter) {
        userApplicationsRepository.saveFilter(filter.toCacheObject())
    }

    suspend fun getFilter(): ApplicationFilter {
        return userApplicationsRepository.getFilter()?.let {
            ApplicationFilter.fromCacheObject(it)
        } ?: ApplicationFilter.default()
    }

    suspend fun getFilterConfig(): ApplicationFilterConfig {
        val filter = getFilter()
        // TODO: receive enabled from storage
        val enabled = false

        return ApplicationFilterConfig(
            enabled = enabled,
            isExclude = filter.mode == ApplicationFilterMode.WHITELIST,
            keys = filter.filterKeys,
        )
    }
}
