package pw.vintr.vintrless.presentation.screen.routing.rulesetList

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import pw.vintr.vintrless.domain.routing.model.Ruleset
import pw.vintr.vintrless.domain.routing.model.Ruleset.Exclude.Type.*
import pw.vintr.vintrless.presentation.theme.Gilroy16
import pw.vintr.vintrless.presentation.theme.RubikMedium14
import pw.vintr.vintrless.presentation.theme.VintrlessExtendedTheme
import pw.vintr.vintrless.presentation.theme.cardShadow
import pw.vintr.vintrless.presentation.uikit.button.ButtonSecondary
import pw.vintr.vintrless.presentation.uikit.button.ButtonSecondarySize
import pw.vintr.vintrless.presentation.uikit.container.RestrictedWidthLayout
import pw.vintr.vintrless.presentation.uikit.layout.ScreenStateLayout
import pw.vintr.vintrless.presentation.uikit.selector.AppRadioButton
import pw.vintr.vintrless.presentation.uikit.toolbar.ToolbarRegular
import vintrless.composeapp.generated.resources.*
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.action_edit
import vintrless.composeapp.generated.resources.ic_delete

@Composable
fun RulesetListScreen(
    viewModel: RulesetListViewModel = koinViewModel()
) {
    val screenState = viewModel.screenState.collectAsState()

    Scaffold(
        topBar = {
            ToolbarRegular(
                title = stringResource(Res.string.rulesets),
                onBackPressed = { viewModel.navigateBack() },
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
                    items(state.payload.rulesets) { ruleset ->
                        RulesetCard(
                            ruleset = ruleset,
                            selected = ruleset.id == state.payload.selectedRulesetId,
                            onSelectClick = {
                                viewModel.selectRuleset(ruleset)
                            },
                            onEditClick = {
                                viewModel.openEditRuleset(ruleset)
                            },
                            onDeleteClick = {
                                viewModel.onDeleteClick(ruleset)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RulesetCard(
    modifier: Modifier = Modifier,
    ruleset: Ruleset,
    selected: Boolean,
    onSelectClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    val borderColor = animateColorAsState(
        targetValue = if (selected) {
            VintrlessExtendedTheme.colors.navBarSelected
        } else {
            VintrlessExtendedTheme.colors.cardStrokeColor
        }
    )
    val shadowColor = animateColorAsState(
        targetValue = if (selected) {
            VintrlessExtendedTheme.colors.navBarSelected
        } else {
            VintrlessExtendedTheme.colors.shadow
        }
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .cardShadow(12.dp, color = shadowColor.value.copy(alpha = 0.25f))
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
                    text = when (ruleset) {
                        is Ruleset.Global -> {
                            stringResource(Res.string.ruleset_global)
                        }
                        is Ruleset.Exclude -> {
                            when (ruleset.type) {
                                BLACKLIST -> {
                                    stringResource(Res.string.ruleset_blacklist)
                                }
                                WHITELIST -> {
                                    stringResource(Res.string.ruleset_whitelist)
                                }
                            }
                        }
                        is Ruleset.Custom -> ruleset.name
                    },
                    color = VintrlessExtendedTheme.colors.textRegular,
                    style = Gilroy16(),
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = when (ruleset) {
                        is Ruleset.Global -> {
                            stringResource(Res.string.ruleset_global_description)
                        }
                        is Ruleset.Exclude -> {
                            when (ruleset.type) {
                                BLACKLIST -> {
                                    stringResource(Res.string.ruleset_blacklist_description)
                                }
                                WHITELIST -> {
                                    stringResource(Res.string.ruleset_whitelist_description)
                                }
                            }
                        }
                        is Ruleset.Custom -> ruleset.description
                    },
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

        if (ruleset is Ruleset.Exclude || ruleset is Ruleset.Custom) {
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

                if (ruleset is Ruleset.Custom) {
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
    }
}
