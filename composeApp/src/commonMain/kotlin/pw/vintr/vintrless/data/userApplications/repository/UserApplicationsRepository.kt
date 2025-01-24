package pw.vintr.vintrless.data.userApplications.repository

import pw.vintr.vintrless.data.userApplications.model.ApplicationFilterCacheObject
import pw.vintr.vintrless.data.userApplications.source.ApplicationFilterCacheDataSource
import pw.vintr.vintrless.data.userApplications.source.SystemProcessCacheDataSource
import pw.vintr.vintrless.domain.userApplications.model.common.process.SystemProcess

class UserApplicationsRepository(
    private val processDataSource: SystemProcessCacheDataSource,
    private val filterDataSource: ApplicationFilterCacheDataSource,
) {

    suspend fun saveSystemProcess(systemProcess: SystemProcess) {
        processDataSource.saveSystemProcess(systemProcess.toCacheObject())
    }

    suspend fun getSavedSystemProcesses(): List<SystemProcess> {
        return processDataSource.getSavedSystemProcesses()
            .map { SystemProcess.fromCacheObject(it) }
    }

    suspend fun saveFilter(applicationFilter: ApplicationFilterCacheObject) {
        filterDataSource.saveFilter(applicationFilter)
    }

    suspend fun getFilter(): ApplicationFilterCacheObject? {
        return filterDataSource.getFilter()
    }
}
