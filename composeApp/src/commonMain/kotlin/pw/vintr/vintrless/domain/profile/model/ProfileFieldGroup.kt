package pw.vintr.vintrless.domain.profile.model

import org.jetbrains.compose.resources.StringResource

data class ProfileFieldGroup(
    val titleRes: StringResource,
    val fields: List<ProfileField>
)
