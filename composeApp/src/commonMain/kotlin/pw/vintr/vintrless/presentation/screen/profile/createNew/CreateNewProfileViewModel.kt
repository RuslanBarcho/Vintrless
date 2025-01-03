package pw.vintr.vintrless.presentation.screen.profile.createNew

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pw.vintr.vintrless.cameraAvailable
import pw.vintr.vintrless.domain.alert.interactor.AlertInteractor
import pw.vintr.vintrless.domain.alert.model.AlertModel
import pw.vintr.vintrless.domain.profile.interactor.ProfileInteractor
import pw.vintr.vintrless.domain.profile.interactor.ProfileUrlInteractor
import pw.vintr.vintrless.domain.profile.model.ProfileForm
import pw.vintr.vintrless.presentation.base.BaseViewModel
import pw.vintr.vintrless.presentation.navigation.AppNavigator
import pw.vintr.vintrless.presentation.navigation.AppScreen

class CreateNewProfileViewModel(
    navigator: AppNavigator,
    private val alertInteractor: AlertInteractor,
    private val profileUrlInteractor: ProfileUrlInteractor,
    private val profileInteractor: ProfileInteractor,
) : BaseViewModel(navigator) {

    private val _screenState = MutableStateFlow(CreateNewProfileState(
        qrScanAvailable = cameraAvailable()
    ))
    val screenState = _screenState.asStateFlow()

    fun openQRScan() {
        navigator.replace(AppScreen.ScanProfileQR)
    }

    fun pasteFromClipboard(pasteText: String) {
        launch(createExceptionHandler {
            alertInteractor.showAlert(AlertModel.CommonError())
            navigateBack()
        }) {
            val profile = profileUrlInteractor.decodeProfileUrl(pasteText)

            // Save to persistent storage
            profileInteractor.saveProfile(profile)

            // Show success message and exit
            alertInteractor.showAlert(AlertModel.ProfileSaveSucceed())
            navigateBack()
        }
    }

    fun openFillProfileForm(form: ProfileForm) {
        navigator.forward(AppScreen.EditProfileForm(form.type.ordinal))
    }
}

data class CreateNewProfileState(
    val availableForms: List<ProfileForm> = ProfileForm.allForms,
    val qrScanAvailable: Boolean = true,
)
