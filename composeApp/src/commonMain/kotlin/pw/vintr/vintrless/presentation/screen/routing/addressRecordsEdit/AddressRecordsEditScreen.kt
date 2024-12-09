package pw.vintr.vintrless.presentation.screen.routing.addressRecordsEdit

import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import pw.vintr.vintrless.domain.routing.model.Ruleset

@Composable
fun AddressRecordsEditScreen(
    rulesetId: String,
    viewModel: AddressRecordsEditViewModel = koinViewModel { parametersOf(rulesetId) }
) {

}
