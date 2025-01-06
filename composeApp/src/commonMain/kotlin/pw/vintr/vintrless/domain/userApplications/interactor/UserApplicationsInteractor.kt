package pw.vintr.vintrless.domain.userApplications.interactor

import pw.vintr.vintrless.domain.base.BaseInteractor
import pw.vintr.vintrless.domain.userApplications.model.UserApplication
import pw.vintr.vintrless.platform.manager.UserApplicationsManager

class UserApplicationsInteractor : BaseInteractor() {

    suspend fun getUserApplications(): List<UserApplication> {
        return UserApplicationsManager.getUserApplications()
    }
}
