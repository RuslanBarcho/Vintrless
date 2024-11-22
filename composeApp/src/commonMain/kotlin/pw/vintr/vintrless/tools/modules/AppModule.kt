package pw.vintr.vintrless.tools.modules

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toSuspendSettings
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import pw.vintr.vintrless.FlowSettings
import pw.vintr.vintrless.data.profile.repository.ProfileRepository
import pw.vintr.vintrless.data.storage.Storage
import pw.vintr.vintrless.data.storage.StorageImpl
import pw.vintr.vintrless.domain.profile.interactor.ProfileInteractor
import pw.vintr.vintrless.presentation.navigation.AppNavigator
import pw.vintr.vintrless.presentation.screen.home.HomeViewModel
import pw.vintr.vintrless.presentation.screen.main.MainViewModel
import pw.vintr.vintrless.presentation.screen.profile.createNew.CreateNewProfileViewModel
import pw.vintr.vintrless.presentation.screen.profile.editForm.EditProfileFormViewModel
import pw.vintr.vintrless.presentation.screen.settings.SettingsViewModel
import pw.vintr.vintrless.tools.extensions.interactor

@OptIn(ExperimentalSettingsApi::class)
val appModule = module {
    single { AppNavigator() }

    // Data
    single<Storage> { StorageImpl(FlowSettings()) }
    single { ProfileRepository(get()) }

    // Domain
    interactor { ProfileInteractor(get()) }

    // Presentation
    viewModel { MainViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { CreateNewProfileViewModel(get()) }
    viewModel { params -> EditProfileFormViewModel(get(), params.get(), params.getOrNull(), get()) }
}
