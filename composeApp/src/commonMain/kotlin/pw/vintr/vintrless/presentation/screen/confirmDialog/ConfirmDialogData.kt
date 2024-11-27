package pw.vintr.vintrless.presentation.screen.confirmDialog

import org.jetbrains.compose.resources.StringResource

sealed interface ConfirmDialogData {

    class Resource(
        val titleRes: StringResource,
        val messageRes: StringResource,
        val acceptTextRes: StringResource? = null,
        val declineTextRes: StringResource? = null,
    ) : ConfirmDialogData

    class Text(
        val title: String,
        val message: String,
        val acceptText: String? = null,
        val declineText: String? = null,
    ) : ConfirmDialogData
}
