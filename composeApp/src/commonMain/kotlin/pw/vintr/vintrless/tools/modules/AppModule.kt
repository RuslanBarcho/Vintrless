package pw.vintr.vintrless.tools.modules

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import pw.vintr.vintrless.presentation.navigation.AppNavigator
import pw.vintr.vintrless.presentation.screen.home.HomeViewModel
import pw.vintr.vintrless.presentation.screen.main.MainViewModel
import pw.vintr.vintrless.presentation.screen.profile.createNew.CreateNewProfileViewModel
import pw.vintr.vintrless.presentation.screen.profile.editForm.EditProfileFormViewModel
import pw.vintr.vintrless.presentation.screen.settings.SettingsViewModel

val appModule = module {
    single { AppNavigator() }

    viewModel { MainViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { CreateNewProfileViewModel(get()) }
    viewModel { params -> EditProfileFormViewModel(get(), params.get(), params.getOrNull()) }
}
