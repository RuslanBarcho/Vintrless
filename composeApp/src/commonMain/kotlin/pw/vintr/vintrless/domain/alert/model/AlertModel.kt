package pw.vintr.vintrless.domain.alert.model

import org.jetbrains.compose.resources.StringResource
import vintrless.composeapp.generated.resources.*
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.copied_to_clipboard
import vintrless.composeapp.generated.resources.error_alert_default_message
import vintrless.composeapp.generated.resources.error_alert_title
import vintrless.composeapp.generated.resources.now_you_can_share_data
import vintrless.composeapp.generated.resources.profile_save_success_message
import vintrless.composeapp.generated.resources.profile_save_success_title

sealed class AlertModel {

    enum class Type {
        POSITIVE,
        NEGATIVE;
    }

    abstract val titleRes: StringResource

    abstract val messageRes: StringResource

    open val type: Type = Type.POSITIVE

    data class CommonError(
        override val titleRes: StringResource = Res.string.error_alert_title,
        override val messageRes: StringResource = Res.string.error_alert_default_message,
    ) : AlertModel() {

        override val type = Type.NEGATIVE
    }

    data class ProfileSaveSucceed(
        override val titleRes: StringResource = Res.string.profile_save_success_title,
        override val messageRes: StringResource = Res.string.profile_save_success_message
    ) : AlertModel() {

        override val type = Type.POSITIVE
    }

    data class DataToShareCopied(
        override val titleRes: StringResource = Res.string.copied_to_clipboard,
        override val messageRes: StringResource = Res.string.now_you_can_share_data
    ) : AlertModel() {

        override val type = Type.POSITIVE
    }

    data class LogToShareCopied(
        override val titleRes: StringResource = Res.string.copied_to_clipboard,
        override val messageRes: StringResource = Res.string.logs_now_you_can_share_data
    ) : AlertModel() {

        override val type = Type.POSITIVE
    }
}
