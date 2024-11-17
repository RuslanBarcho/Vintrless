package pw.vintr.vintrless.presentation.screen.profile.editForm

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.profile.model.ProfileField
import pw.vintr.vintrless.domain.profile.model.ProfileForm
import pw.vintr.vintrless.domain.profile.model.ProfileType
import pw.vintr.vintrless.presentation.base.BaseViewModel
import pw.vintr.vintrless.presentation.navigation.AppNavigator

class EditProfileFormViewModel(
    navigator: AppNavigator,
    profileType: ProfileType,
    dataId: String? = null,
) : BaseViewModel(navigator) {

    private val _screenState = MutableStateFlow(EditProfileFormState(
        form = ProfileForm.getByType(profileType),
        data = ProfileData(type = profileType)
    ))
    val screenState = _screenState.asStateFlow()

    fun setValue(field: ProfileField, value: String?) {
        _screenState.update { state ->
            state.copy(data = state.data.setField(field, value))
        }
    }

    fun save() {
        // TODO: logic
    }
}

data class EditProfileFormState(
    val form: ProfileForm,
    val data: ProfileData,
)
