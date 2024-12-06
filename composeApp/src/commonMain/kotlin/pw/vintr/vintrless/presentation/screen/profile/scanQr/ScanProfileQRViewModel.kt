package pw.vintr.vintrless.presentation.screen.profile.scanQr

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import pw.vintr.vintrless.domain.alert.interactor.AlertInteractor
import pw.vintr.vintrless.domain.alert.model.AlertModel
import pw.vintr.vintrless.domain.profile.interactor.ProfileInteractor
import pw.vintr.vintrless.domain.profile.useCase.decodeUrl.DecodeVlessUrlUseCase
import pw.vintr.vintrless.presentation.base.BaseViewModel
import pw.vintr.vintrless.presentation.navigation.AppNavigator
import kotlin.time.Duration.Companion.milliseconds

class ScanProfileQRViewModel(
    navigator: AppNavigator,
    private val profileInteractor: ProfileInteractor,
    private val alertInteractor: AlertInteractor,
) : BaseViewModel(navigator) {

    private val scanResultFlow = MutableStateFlow<String?>(value = null)

    init {
        subscribeScanResults()
    }

    fun onTextScanned(scannedText: String) {
        scanResultFlow.value = scannedText
    }

    @OptIn(FlowPreview::class)
    private fun subscribeScanResults() {
        launch {
            scanResultFlow
                .filterNotNull()
                .debounce(500.milliseconds)
                .collectLatest {
                    decodeScannedUrl(it)
                }
        }
    }

    private fun decodeScannedUrl(scannedText: String) {
        launch(createExceptionHandler { alertInteractor.showAlert(AlertModel.CommonError()) }) {
            val decodedProfile = DecodeVlessUrlUseCase.invoke(scannedText)
            profileInteractor.saveProfile(decodedProfile)

            alertInteractor.showAlert(AlertModel.ProfileSaveSucceed())
            navigator.back()
        }
    }
}
