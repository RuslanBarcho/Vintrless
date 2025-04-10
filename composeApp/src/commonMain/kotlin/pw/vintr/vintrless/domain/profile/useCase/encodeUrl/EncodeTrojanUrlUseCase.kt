package pw.vintr.vintrless.domain.profile.useCase.encodeUrl

import pw.vintr.vintrless.domain.profile.model.ProfileData
import pw.vintr.vintrless.domain.profile.model.ProfileField
import pw.vintr.vintrless.domain.profile.useCase.encodeUrl.base.BaseEncodeProfileUrlUseCase

object EncodeTrojanUrlUseCase : BaseEncodeProfileUrlUseCase() {

    override fun invoke(profile: ProfileData): String {
        return encode(
            profile = profile,
            userInfo = profile.data[ProfileField.Password.key].orEmpty()
        )
    }
}
