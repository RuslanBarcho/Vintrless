package pw.vintr.vintrless.presentation.screen.profile.editForm

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pw.vintr.vintrless.domain.profile.interactor.ProfileInteractor
import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.profile.model.ProfileField
import pw.vintr.vintrless.domain.profile.model.ProfileForm
import pw.vintr.vintrless.domain.profile.model.ProfileType
import pw.vintr.vintrless.presentation.base.BaseScreenState
import pw.vintr.vintrless.presentation.base.BaseViewModel
import pw.vintr.vintrless.presentation.navigation.AppNavigator
import pw.vintr.vintrless.tools.extensions.updateLoaded
import pw.vintr.vintrless.tools.extensions.withLoaded

class EditProfileFormViewModel(
    navigator: AppNavigator,
    private val profileType: ProfileType,
    private val dataId: String? = null,
    private val profileInteractor: ProfileInteractor,
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
            val form = ProfileForm.getByType(profileType)

            if (dataId != null) {
                EditProfileFormState(
                    form = form,
                    data = ProfileData(type = profileType)
                )
            } else {
                EditProfileFormState(
                    form = form,
                    data = ProfileData(
                        type = profileType,
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
                withLoading(
                    setLoadingCallback = { isLoading ->
                        _screenState.updateLoaded { it.copy(isSaving = isLoading) }
                    },
                    action = {
                        profileInteractor.saveProfile(state.data)
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
