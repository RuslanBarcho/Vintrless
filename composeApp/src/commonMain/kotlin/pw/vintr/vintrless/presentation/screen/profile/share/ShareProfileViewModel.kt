package pw.vintr.vintrless.presentation.screen.profile.share

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pw.vintr.vintrless.domain.alert.interactor.AlertInteractor
import pw.vintr.vintrless.domain.alert.model.AlertModel
import pw.vintr.vintrless.domain.profile.interactor.ProfileInteractor
import pw.vintr.vintrless.domain.profile.useCase.encodeUrl.EncodeProfileUrlUseCase
import pw.vintr.vintrless.domain.v2ray.useCase.V2RayConfigBuildUseCase
import pw.vintr.vintrless.platform.ShareActionManager
import pw.vintr.vintrless.presentation.base.BaseScreenState
import pw.vintr.vintrless.presentation.base.BaseViewModel
import pw.vintr.vintrless.presentation.navigation.AppNavigator
import pw.vintr.vintrless.tools.extensions.withLoaded

class ShareProfileViewModel(
    navigator: AppNavigator,
    private val dataId: String,
    private val profileInteractor: ProfileInteractor,
    private val alertInteractor: AlertInteractor,
) : BaseViewModel(navigator) {

    private val _screenState = MutableStateFlow<BaseScreenState<ShareProfileState>>(
        BaseScreenState.Loading()
    )
    val screenState = _screenState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        _screenState.loadWithStateHandling {
            val data = requireNotNull(profileInteractor.getProfile(dataId))

            ShareProfileState(
                profileUrl = EncodeProfileUrlUseCase(data),
                profileJSON = V2RayConfigBuildUseCase(data).configJson
            )
        }
    }

    fun performShareAction(content: String) {
        _screenState.withLoaded {
            ShareActionManager.shareText(content)

            if (!ShareActionManager.canOpenActionSheet) {
                alertInteractor.showAlert(AlertModel.DataToShareCopied())
                navigator.back()
            }
        }
    }
}

data class ShareProfileState(
    val profileUrl: String,
    val profileJSON: String,
)
