package pw.vintr.vintrless.presentation.screen.profile.list

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.presentation.theme.*
import pw.vintr.vintrless.presentation.uikit.button.ButtonSecondary
import pw.vintr.vintrless.presentation.uikit.button.ButtonSecondarySize
import pw.vintr.vintrless.presentation.uikit.container.RestrictedWidthLayout
import pw.vintr.vintrless.presentation.uikit.layout.ScreenStateLayout
import pw.vintr.vintrless.presentation.uikit.selector.AppRadioButton
import pw.vintr.vintrless.presentation.uikit.toolbar.ToolbarRegular
import pw.vintr.vintrless.tools.extensions.Dot
import pw.vintr.vintrless.tools.extensions.Space
import pw.vintr.vintrless.tools.extensions.selectableCardBackground
import vintrless.composeapp.generated.resources.*
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.action_edit
import vintrless.composeapp.generated.resources.ic_add
import vintrless.composeapp.generated.resources.ic_delete
import vintrless.composeapp.generated.resources.ic_share
import vintrless.composeapp.generated.resources.profile_list_title

@Composable
fun ProfileListScreen(
    viewModel: ProfileListViewModel = koinViewModel(),
) {
    val screenState = viewModel.screenState.collectAsState()

    Scaffold(
        topBar = {
            ToolbarRegular(
                title = stringResource(Res.string.profile_list_title),
                onBackPressed = { viewModel.navigateBack() },
                trailing = {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .clickable { viewModel.openCreateNewProfile() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(24.dp),
                            painter = painterResource(Res.drawable.ic_add),
                            contentDescription = "Add new profile button icon",
                            tint = VintrlessExtendedTheme.colors.textRegular
                        )
                    }
                }
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
                if (state.payload.profiles.isNotEmpty()) {
                    // Profile items
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        contentPadding = PaddingValues(
                            vertical = 20.dp,
                            horizontal = 28.dp,
                        )
                    ) {
                        items(state.payload.profiles) { profile ->
                            ProfileCard(
                                profileData = profile,
                                selected = profile == state.payload.selectedProfile,
                                onSelectClick = {
                                    viewModel.selectProfile(profile)
                                },
                                onEditClick = {
                                    viewModel.openEditProfile(profile)
                                },
                                onShareClick = {
                                    viewModel.onShareClick(profile)
                                },
                                onDeleteClick = {
                                    viewModel.onDeleteClick(profile)
                                }
                            )
                        }
                    }
                } else {
                    // Empty state
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 28.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.illustration_profile),
                            contentDescription = null,
                        )
                        Spacer(modifier = Modifier.height(28.dp))
                        Text(
                            modifier = Modifier
                                .fillMaxWidth(),
                            text = stringResource(Res.string.no_config_description),
                            color = VintrlessExtendedTheme.colors.textRegular,
                            style = RubikMedium16(),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileCard(
    modifier: Modifier = Modifier,
    profileData: ProfileData,
    selected: Boolean,
    onSelectClick: () -> Unit,
    onEditClick: () -> Unit,
    onShareClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .selectableCardBackground(selected = selected)
            .clickable { onSelectClick() }
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = profileData.name,
                    color = VintrlessExtendedTheme.colors.textRegular,
                    style = Gilroy16(),
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = profileData.ip +
                            String.Space +
                            String.Dot +
                            String.Space +
                            profileData.type.protocolName,
                    color = VintrlessExtendedTheme.colors.textSecondary,
                    style = RubikMedium14(),
                )
            }
            Spacer(Modifier.width(20.dp))
            AppRadioButton(
                selected = selected,
                onClick = { onSelectClick() }
            )
        }
        Spacer(Modifier.height(24.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ButtonSecondary(
                modifier = Modifier
                    .weight(1f),
                text = stringResource(Res.string.action_edit),
                size = ButtonSecondarySize.MEDIUM,
            ) { onEditClick() }
            Spacer(Modifier.width(8.dp))
            ButtonSecondary(
                wrapContentWidth = true,
                size = ButtonSecondarySize.MEDIUM,
                content = {
                    Icon(
                        modifier = Modifier
                            .size(24.dp),
                        painter = painterResource(Res.drawable.ic_share),
                        tint = VintrlessExtendedTheme.colors.secondaryButtonContent,
                        contentDescription = null
                    )
                }
            ) { onShareClick() }
            Spacer(Modifier.width(8.dp))
            ButtonSecondary(
                wrapContentWidth = true,
                size = ButtonSecondarySize.MEDIUM,
                content = {
                    Icon(
                        modifier = Modifier
                            .size(24.dp),
                        painter = painterResource(Res.drawable.ic_delete),
                        tint = VintrlessExtendedTheme.colors.secondaryButtonContent,
                        contentDescription = null
                    )
                }
            ) { onDeleteClick() }
        }
    }
}
