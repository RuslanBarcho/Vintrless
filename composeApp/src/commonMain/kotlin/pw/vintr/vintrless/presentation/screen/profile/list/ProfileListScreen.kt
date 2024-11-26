package pw.vintr.vintrless.presentation.screen.profile.list

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.presentation.theme.Gilroy16
import pw.vintr.vintrless.presentation.theme.VintrlessExtendedTheme
import pw.vintr.vintrless.presentation.uikit.container.RestrictedWidthLayout
import pw.vintr.vintrless.presentation.uikit.layout.ScreenStateLayout
import pw.vintr.vintrless.presentation.uikit.selector.AppRadioButton
import pw.vintr.vintrless.presentation.uikit.toolbar.ToolbarRegular
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.ic_add
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
                            }
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
) {
    val borderColor = animateColorAsState(
        targetValue = if (selected) {
            VintrlessExtendedTheme.colors.navBarSelected
        } else {
            VintrlessExtendedTheme.colors.cardStrokeColor
        }
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(VintrlessExtendedTheme.colors.cardBackgroundColor)
            .border(BorderStroke(1.dp, borderColor.value), RoundedCornerShape(12.dp))
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
                    text = profileData.ip,
                    color = VintrlessExtendedTheme.colors.textSecondary,
                    style = Gilroy16(),
                )
            }
            Spacer(Modifier.width(20.dp))
            AppRadioButton(
                selected = selected,
                onClick = { onSelectClick() }
            )
        }
    }
}
