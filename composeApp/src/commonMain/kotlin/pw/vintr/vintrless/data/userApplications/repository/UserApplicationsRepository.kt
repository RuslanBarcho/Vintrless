package pw.vintr.vintrless.data.userApplications.repository

import pw.vintr.vintrless.data.storage.preference.PreferenceStorage
import pw.vintr.vintrless.data.userApplications.model.ApplicationFilterCacheObject
import pw.vintr.vintrless.data.userApplications.model.SystemProcessCacheObject
import pw.vintr.vintrless.data.userApplications.source.ApplicationFilterCacheDataSource
import pw.vintr.vintrless.data.userApplications.source.SystemProcessCacheDataSource

class UserApplicationsRepository(
    private val processDataSource: SystemProcessCacheDataSource,
    private val filterDataSource: ApplicationFilterCacheDataSource,
    private val preferenceStorage: PreferenceStorage,
) {

    companion object {
        private const val APPLICATION_FILTER_ENABLED_KEY = "application_filter_enabled"
    }

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

    suspend fun saveFilterEnabled(value: Boolean) {
        preferenceStorage.saveBoolean(APPLICATION_FILTER_ENABLED_KEY, value)
    }

    suspend fun getFilterEnabled(): Boolean {
        return preferenceStorage.getBoolean(APPLICATION_FILTER_ENABLED_KEY)
    }
}
