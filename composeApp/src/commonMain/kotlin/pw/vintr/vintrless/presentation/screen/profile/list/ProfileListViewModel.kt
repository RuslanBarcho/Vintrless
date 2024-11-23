package pw.vintr.vintrless.presentation.screen.profile.list

import pw.vintr.vintrless.domain.profile.interactor.ProfileInteractor
import pw.vintr.vintrless.presentation.base.BaseViewModel
import pw.vintr.vintrless.presentation.navigation.AppNavigator

class ProfileListViewModel(
    navigator: AppNavigator,
    val profileInteractor: ProfileInteractor,
) : BaseViewModel(navigator) {


}
