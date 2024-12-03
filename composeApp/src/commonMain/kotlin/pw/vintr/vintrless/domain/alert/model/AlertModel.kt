package pw.vintr.vintrless.domain.alert.model

import org.jetbrains.compose.resources.StringResource
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.error_alert_title
import vintrless.composeapp.generated.resources.error_alert_default_message
import vintrless.composeapp.generated.resources.profile_save_success_title
import vintrless.composeapp.generated.resources.profile_save_success_message

sealed class AlertModel {

    abstract val titleRes: StringResource

    abstract val messageRes: StringResource

    data class CommonError(
        override val titleRes: StringResource = Res.string.error_alert_title,
        override val messageRes: StringResource = Res.string.error_alert_default_message
    ) : AlertModel()

    data class ProfileSaveSucceed(
        override val titleRes: StringResource = Res.string.profile_save_success_title,
        override val messageRes: StringResource = Res.string.profile_save_success_message
    ) : AlertModel()
}
