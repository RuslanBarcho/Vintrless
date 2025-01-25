package pw.vintr.vintrless.data.userApplications.repository

import pw.vintr.vintrless.data.userApplications.model.ApplicationFilterCacheObject
import pw.vintr.vintrless.data.userApplications.model.SystemProcessCacheObject
import pw.vintr.vintrless.data.userApplications.source.ApplicationFilterCacheDataSource
import pw.vintr.vintrless.data.userApplications.source.SystemProcessCacheDataSource
import pw.vintr.vintrless.domain.userApplications.model.common.process.SystemProcess

class UserApplicationsRepository(
    private val processDataSource: SystemProcessCacheDataSource,
    private val filterDataSource: ApplicationFilterCacheDataSource,
) {

    suspend fun saveSystemProcess(systemProcess: SystemProcessCacheObject) {
        processDataSource.saveSystemProcess(systemProcess)
    }

    suspend fun getSavedSystemProcesses(): List<SystemProcessCacheObject> {
        return processDataSource.getSavedSystemProcesses()
    }

    suspend fun saveFilter(applicationFilter: ApplicationFilterCacheObject) {
        filterDataSource.saveFilter(applicationFilter)
    }

    suspend fun getFilter(): ApplicationFilterCacheObject? {
        return filterDataSource.getFilter()
    }
}
