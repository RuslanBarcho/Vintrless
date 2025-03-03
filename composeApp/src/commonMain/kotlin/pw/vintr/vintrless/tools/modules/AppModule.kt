package pw.vintr.vintrless.tools.modules

import com.russhwolf.settings.ExperimentalSettingsApi
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.migration.AutomaticSchemaMigration
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.dsl.onClose
import pw.vintr.vintrless.FlowSettings
import pw.vintr.vintrless.LogPlatformInteractor
import pw.vintr.vintrless.data.profile.model.ProfileDataCacheObject
import pw.vintr.vintrless.data.profile.repository.ProfileRepository
import pw.vintr.vintrless.data.profile.source.ProfileCacheDataSource
import pw.vintr.vintrless.data.routing.model.ExcludeRulesetCacheObject
import pw.vintr.vintrless.data.routing.repository.RoutingRepository
import pw.vintr.vintrless.data.routing.source.ExcludeRulesetCacheDataSource
import pw.vintr.vintrless.data.storage.preference.PreferenceStorage
import pw.vintr.vintrless.data.storage.preference.PreferenceStorageImpl
import pw.vintr.vintrless.data.userApplications.model.ApplicationFilterCacheObject
import pw.vintr.vintrless.data.userApplications.model.SystemProcessCacheObject
import pw.vintr.vintrless.data.userApplications.repository.UserApplicationsRepository
import pw.vintr.vintrless.data.userApplications.source.ApplicationFilterCacheDataSource
import pw.vintr.vintrless.data.userApplications.source.SystemProcessCacheDataSource
import pw.vintr.vintrless.domain.alert.interactor.AlertInteractor
import pw.vintr.vintrless.domain.profile.interactor.ProfileInteractor
import pw.vintr.vintrless.domain.profile.interactor.ProfileUrlInteractor
import pw.vintr.vintrless.domain.routing.interactor.RoutingInteractor
import pw.vintr.vintrless.domain.userApplications.interactor.UserApplicationsInteractor
import pw.vintr.vintrless.domain.v2ray.interactor.V2RayConnectionInteractor
import pw.vintr.vintrless.platform.manager.RealmConfigurationManager.applyPlatformConfiguration
import pw.vintr.vintrless.presentation.navigation.AppNavigator
import pw.vintr.vintrless.presentation.screen.about.AboutAppViewModel
import pw.vintr.vintrless.presentation.screen.applicationFilter.ApplicationFilterViewModel
import pw.vintr.vintrless.presentation.screen.confirmDialog.ConfirmViewModel
import pw.vintr.vintrless.presentation.screen.home.HomeViewModel
import pw.vintr.vintrless.presentation.screen.log.filter.LogFilterViewModel
import pw.vintr.vintrless.presentation.screen.log.viewer.LogViewerViewModel
import pw.vintr.vintrless.presentation.screen.main.MainViewModel
import pw.vintr.vintrless.presentation.screen.profile.createNew.CreateNewProfileViewModel
import pw.vintr.vintrless.presentation.screen.profile.editForm.EditProfileFormViewModel
import pw.vintr.vintrless.presentation.screen.profile.list.ProfileListViewModel
import pw.vintr.vintrless.presentation.screen.profile.scanQr.ScanProfileQRViewModel
import pw.vintr.vintrless.presentation.screen.profile.share.ShareProfileViewModel
import pw.vintr.vintrless.presentation.screen.routing.addressRecords.addRecords.AddAddressRecordsViewModel
import pw.vintr.vintrless.presentation.screen.routing.addressRecords.editRecords.EditAddressRecordsViewModel
import pw.vintr.vintrless.presentation.screen.routing.addressRecords.manualInputRecords.ManualInputAddressRecordsViewModel
import pw.vintr.vintrless.presentation.screen.routing.rulesetList.RulesetListViewModel
import pw.vintr.vintrless.presentation.screen.settings.SettingsViewModel
import pw.vintr.vintrless.tools.extensions.interactor

private const val REALM_SCHEMA_VERSION = 2L

val realmMigration = AutomaticSchemaMigration { _ -> }

@OptIn(ExperimentalSettingsApi::class)
val appModule = module {
    single { AppNavigator() }

    // Realm
    single {
        val config = RealmConfiguration.Builder(
            schema = setOf(
                ProfileDataCacheObject::class,
                ExcludeRulesetCacheObject::class,
                SystemProcessCacheObject::class,
                ApplicationFilterCacheObject::class,
            )
        )
            .schemaVersion(REALM_SCHEMA_VERSION)
            .migration(realmMigration)
            .applyPlatformConfiguration()
            .build()

        Realm.open(config)
    } onClose { realm ->
        realm?.close()
    }

    // Data
    val flowSettings = FlowSettings()
    single<PreferenceStorage> { PreferenceStorageImpl(flowSettings) }

    single { ProfileCacheDataSource(get()) }
    single { ProfileRepository(get(), get()) }

    single { ExcludeRulesetCacheDataSource(get()) }
    single { RoutingRepository(get(), get()) }

    single { SystemProcessCacheDataSource(get()) }
    single { ApplicationFilterCacheDataSource(get()) }
    single { UserApplicationsRepository(get(), get(), get()) }

    // Domain
    val logInteractor = LogPlatformInteractor()

    interactor { logInteractor }
    interactor { AlertInteractor() }
    interactor { ProfileInteractor(get()) }
    interactor { ProfileUrlInteractor() }
    interactor { RoutingInteractor(get()) }
    interactor {
        V2RayConnectionInteractor(
            profileInteractor = get(),
            routingInteractor = get(),
            userApplicationInteractor = get(),
        )
    }
    interactor { UserApplicationsInteractor(get()) }

    // Presentation
    viewModel { MainViewModel(get()) }
    viewModel { ConfirmViewModel(get()) }
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { CreateNewProfileViewModel(get(), get(), get(), get()) }
    viewModel { ScanProfileQRViewModel(get(), get(), get()) }
    viewModel { params ->
        EditProfileFormViewModel(
            navigator = get(),
            protocolType = params.get(),
            dataId = params.getOrNull(),
            profileInteractor = get(),
            alertInteractor = get(),
            v2RayConnectionInteractor = get(),
        )
    }
    viewModel { ProfileListViewModel(get(), get(), get()) }
    viewModel { params ->
        ShareProfileViewModel(
            navigator = get(),
            dataId = params.get(),
            profileInteractor = get(),
            alertInteractor = get(),
        )
    }
    viewModel { RulesetListViewModel(get(), get(), get()) }
    viewModel { params ->
        EditAddressRecordsViewModel(
            navigator = get(),
            rulesetId = params.get(),
            routingInteractor = get(),
        )
    }
    viewModel { AddAddressRecordsViewModel(get()) }
    viewModel { params ->
        ManualInputAddressRecordsViewModel(
            navigator = get(),
            defaultReplaceCurrent = params.get(),
        )
    }
    viewModel { AboutAppViewModel(get()) }
    viewModel { ApplicationFilterViewModel(get(), get(), get()) }
    viewModel { LogViewerViewModel(get(), get(), get()) }
    viewModel { params ->
        LogFilterViewModel(
            navigator = get(),
            logFilter = params.get(),
        )
    }
}
