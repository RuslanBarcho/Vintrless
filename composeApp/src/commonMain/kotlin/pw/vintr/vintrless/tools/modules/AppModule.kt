package pw.vintr.vintrless.tools.modules

import com.russhwolf.settings.ExperimentalSettingsApi
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import pw.vintr.vintrless.FlowSettings
import pw.vintr.vintrless.data.profile.model.ProfileDataStorageObject
import pw.vintr.vintrless.data.profile.repository.ProfileRepository
import pw.vintr.vintrless.data.storage.collection.CollectionStorage
import pw.vintr.vintrless.data.storage.collection.ProfileDataStorage
import pw.vintr.vintrless.data.storage.preference.PreferenceStorage
import pw.vintr.vintrless.data.storage.preference.PreferenceStorageImpl
import pw.vintr.vintrless.domain.alert.interactor.AlertInteractor
import pw.vintr.vintrless.domain.profile.interactor.ProfileInteractor
import pw.vintr.vintrless.presentation.navigation.AppNavigator
import pw.vintr.vintrless.presentation.screen.confirmDialog.ConfirmViewModel
import pw.vintr.vintrless.presentation.screen.home.HomeViewModel
import pw.vintr.vintrless.presentation.screen.main.MainViewModel
import pw.vintr.vintrless.presentation.screen.profile.createNew.CreateNewProfileViewModel
import pw.vintr.vintrless.presentation.screen.profile.editForm.EditProfileFormViewModel
import pw.vintr.vintrless.presentation.screen.profile.list.ProfileListViewModel
import pw.vintr.vintrless.presentation.screen.settings.SettingsViewModel
import pw.vintr.vintrless.tools.extensions.interactor

@OptIn(ExperimentalSettingsApi::class)
val appModule = module {
    single { AppNavigator() }

    // Data
    val flowSettings = FlowSettings()

    single<PreferenceStorage> { PreferenceStorageImpl(flowSettings) }
    single<CollectionStorage<ProfileDataStorageObject>> { ProfileDataStorage(flowSettings) }
    single { ProfileRepository(get(), get()) }

    // Domain
    interactor { AlertInteractor() }
    interactor { ProfileInteractor(get()) }

    // Presentation
    viewModel { MainViewModel(get()) }
    viewModel { ConfirmViewModel(get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { CreateNewProfileViewModel(get()) }
    viewModel { params ->
        EditProfileFormViewModel(
            navigator = get(),
            protocolType = params.get(),
            dataId = params.getOrNull(),
            profileInteractor = get(),
            alertInteractor = get()
        )
    }
    viewModel { ProfileListViewModel(get(), get()) }
}
