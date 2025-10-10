package com.softwama.goplan.navigation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class NavigationViewModel : ViewModel() {

    sealed class NavigationCommand {
        data class NavigateTo(val route: String, val options: NavigationOptions? = null) : NavigationCommand()
        object PopBackStack : NavigationCommand()
    }

    private val _navigationCommand = Channel<NavigationCommand>(Channel.BUFFERED)
    val navigationCommand = _navigationCommand.receiveAsFlow()

    fun navigateTo(route: String, options: NavigationOptions? = null) {
        _navigationCommand.trySend(NavigationCommand.NavigateTo(route, options))
    }

    fun popBackStack() {
        _navigationCommand.trySend(NavigationCommand.PopBackStack)
    }
}

enum class NavigationOptions {
    NORMAL,
    CLEAR_BACK_STACK,
    REPLACE_HOME
}
