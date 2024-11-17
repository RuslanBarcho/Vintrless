package pw.vintr.vintrless.presentation.screen.profile.createNew

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pw.vintr.vintrless.domain.profile.model.ProfileForm
import pw.vintr.vintrless.presentation.base.BaseViewModel
import pw.vintr.vintrless.presentation.navigation.AppNavigator
import pw.vintr.vintrless.presentation.navigation.AppScreen

class CreateNewProfileViewModel(
    navigator: AppNavigator
) : BaseViewModel(navigator) {

    private val _screenState = MutableStateFlow(CreateNewProfileState())
    val screenState = _screenState.asStateFlow()

    fun openQRScan() {

    }

    fun pasteFromClipboard() {
        // TODO: logic
    }

    fun openFillProfileForm(form: ProfileForm) {
        navigator.forward(AppScreen.EditProfileForm(form.type.ordinal))
    }
}

data class CreateNewProfileState(
    val availableForms: List<ProfileForm> = ProfileForm.allForms,
    val qrScanAvailable: Boolean = true,
)
