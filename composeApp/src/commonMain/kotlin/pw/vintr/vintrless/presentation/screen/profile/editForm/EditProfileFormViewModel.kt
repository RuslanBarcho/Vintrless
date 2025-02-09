package pw.vintr.vintrless.presentation.screen.profile.editForm

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pw.vintr.vintrless.domain.alert.interactor.AlertInteractor
import pw.vintr.vintrless.domain.alert.model.AlertModel
import pw.vintr.vintrless.domain.profile.interactor.ProfileInteractor
import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.profile.model.ProfileField
import pw.vintr.vintrless.domain.profile.model.ProfileForm
import pw.vintr.vintrless.domain.v2ray.interactor.V2RayConnectionInteractor
import pw.vintr.vintrless.domain.v2ray.model.ProtocolType
import pw.vintr.vintrless.presentation.base.BaseScreenState
import pw.vintr.vintrless.presentation.base.BaseViewModel
import pw.vintr.vintrless.presentation.navigation.AppNavigator
import pw.vintr.vintrless.tools.extensions.updateLoaded
import pw.vintr.vintrless.tools.extensions.withLoaded

class EditProfileFormViewModel(
    navigator: AppNavigator,
    private val protocolType: ProtocolType,
    private val dataId: String? = null,
    private val profileInteractor: ProfileInteractor,
    private val alertInteractor: AlertInteractor,
    private val v2RayConnectionInteractor: V2RayConnectionInteractor,
) : BaseViewModel(navigator) {

    private val _screenState = MutableStateFlow<BaseScreenState<EditProfileFormState>>(
        BaseScreenState.Loading()
    )
    val screenState = _screenState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        _screenState.loadWithStateHandling {
            val form = ProfileForm.getByType(protocolType)

            if (dataId != null) {
                val data = profileInteractor.getProfile(dataId)

                EditProfileFormState(
                    form = form,
                    data = requireNotNull(data)
                )
            } else {
                EditProfileFormState(
                    form = form,
                    data = ProfileData(
                        type = protocolType,
                        data = form.getDefaultData()
                    )
                )
            }
        }
    }

    fun setValue(field: ProfileField, value: String?) {
        _screenState.updateLoaded { state ->
            state.copy(data = state.data.setField(field, value))
        }
    }

    fun save() {
        launch {
            _screenState.withLoaded { state ->
                val profile = state.data

                withLoading(
                    setLoadingCallback = { isLoading ->
                        _screenState.updateLoaded { it.copy(isSaving = isLoading) }
                    },
                    action = {
                        // Save to persistent storage
                        profileInteractor.saveProfile(profile)

                        // Apply configuration to platform
                        v2RayConnectionInteractor.applyConfiguration()

                        // Show success message and exit
                        alertInteractor.showAlert(AlertModel.ProfileSaveSucceed())
                        navigateBack()
                    }
                )
            }
        }
    }
}

data class EditProfileFormState(
    val form: ProfileForm,
    val data: ProfileData,
    val isSaving: Boolean = false,
)
