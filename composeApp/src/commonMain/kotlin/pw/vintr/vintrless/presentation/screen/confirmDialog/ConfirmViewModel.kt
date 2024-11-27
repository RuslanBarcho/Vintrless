package pw.vintr.vintrless.presentation.screen.confirmDialog

import pw.vintr.vintrless.presentation.base.BaseViewModel
import pw.vintr.vintrless.presentation.navigation.AppNavigator
import pw.vintr.vintrless.presentation.navigation.NavigatorType

class ConfirmViewModel(
    navigator: AppNavigator,
) : BaseViewModel(navigator) {

    fun accept() {
        navigator.back(
            type = NavigatorType.Root,
            resultKey = ConfirmResult.KEY,
            result = ConfirmResult.ACCEPT
        )
    }

    fun decline() {
        navigator.back(
            type = NavigatorType.Root,
            resultKey = ConfirmResult.KEY,
            result = ConfirmResult.DECLINE
        )
    }
}
