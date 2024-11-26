package pw.vintr.vintrless.presentation.screen.profile.editForm

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.profile.model.ProfileField
import pw.vintr.vintrless.domain.profile.model.ProfileType
import pw.vintr.vintrless.presentation.theme.Gilroy18
import pw.vintr.vintrless.presentation.theme.VintrlessExtendedTheme
import pw.vintr.vintrless.presentation.uikit.button.ButtonRegular
import pw.vintr.vintrless.presentation.uikit.container.RestrictedWidthLayout
import pw.vintr.vintrless.presentation.uikit.input.AppDropdownField
import pw.vintr.vintrless.presentation.uikit.input.AppTextField
import pw.vintr.vintrless.presentation.uikit.input.DropdownPayload
import pw.vintr.vintrless.presentation.uikit.input.SwitchField
import pw.vintr.vintrless.presentation.uikit.layout.ScreenStateLayout
import pw.vintr.vintrless.presentation.uikit.toolbar.ToolbarRegular
import pw.vintr.vintrless.tools.extensions.isBoolean
import pw.vintr.vintrless.tools.extensions.stringValue
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.common_save
import vintrless.composeapp.generated.resources.field_not_specified

@Composable
fun EditProfileFormScreen(
    profileType: ProfileType,
    dataId: String? = null,
    viewModel: EditProfileFormViewModel = koinViewModel { parametersOf(profileType, dataId) },
) {
    val screenState = viewModel.screenState.collectAsState()

    @Composable
    fun RenderFields(
        fields: List<ProfileField>,
        data: ProfileData
    ) {
        fields.forEachIndexed { index, profileField ->
            val fieldValue = data.getField(profileField)

            Field(
                profileField = profileField,
                value = data.getField(profileField)
            ) { viewModel.setValue(profileField, it) }

            if (index != fields.lastIndex) {
                Spacer(modifier = Modifier.height(16.dp))
            }

            profileField.subfieldsByValue[fieldValue]?.let { subfields ->
                Spacer(modifier = Modifier.height(16.dp))
                RenderFields(subfields, data)
            }
        }
    }

    Scaffold(
        topBar = {
            ToolbarRegular(
                title = profileType.profileName,
                onBackPressed = { viewModel.navigateBack() }
            )
        },
    ) { scaffoldPadding ->
        RestrictedWidthLayout(
            restrictionWidth = 800.dp
        ) {
            ScreenStateLayout(
                modifier = Modifier
                    .padding(scaffoldPadding)
                    .fillMaxSize(),
                state = screenState.value
            ) { state ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Column (
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 28.dp, vertical = 20.dp),
                    ) {
                        state.payload.form.fieldGroups.forEachIndexed { index, profileFieldGroup ->
                            Text(
                                text = stringResource(profileFieldGroup.titleRes),
                                color = VintrlessExtendedTheme.colors.textRegular,
                                style = Gilroy18()
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            RenderFields(profileFieldGroup.fields, state.payload.data)

                            if (index != state.payload.form.fieldGroups.lastIndex) {
                                Spacer(modifier = Modifier.height(48.dp))
                            }
                        }
                    }
                    ButtonRegular(
                        modifier = Modifier
                            .padding(start = 28.dp, end = 28.dp, top = 12.dp, bottom = 20.dp),
                        text = stringResource(Res.string.common_save),
                        isLoading = state.payload.isSaving,
                    ) {
                        viewModel.save()
                    }
                }
            }
        }
    }
}

@Composable
private fun Field(
    profileField: ProfileField,
    value: String?,
    onSetValue: (String?) -> Unit
) {
    val availableValues = profileField.availableValues ?: listOf()

    if (availableValues.isNotEmpty()) {
        if (availableValues.all { it != null && it.isBoolean() }) {
            SwitchField(
                modifier = Modifier
                    .fillMaxWidth(),
                title = stringResource(profileField.titleRes),
                checked = value.toBoolean(),
                onCheckedChange = { onSetValue(it.stringValue()) }
            )
        } else {
            AppDropdownField(
                modifier = Modifier
                    .fillMaxWidth(),
                items = availableValues.map { availableValue ->
                    DropdownPayload(
                        payload = availableValue,
                        title = availableValue ?: stringResource(Res.string.field_not_specified)
                    )
                },
                selectedItem =  DropdownPayload(
                    payload = value,
                    title = value ?: stringResource(Res.string.field_not_specified)
                ),
                label = stringResource(profileField.titleRes),
                onItemSelected = { onSetValue(it) },
            )
        }
    } else {
        AppTextField(
            modifier = Modifier
                .fillMaxWidth(),
            label = stringResource(profileField.titleRes),
            value = value.orEmpty(),
            hint = stringResource(Res.string.field_not_specified),
            onValueChange = { onSetValue(it) }
        )
    }
}
