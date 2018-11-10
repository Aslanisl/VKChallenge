package ru.mail.aslanisl.vkchallenge.extensions

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import ru.mail.aslanisl.vkchallenge.ui.base.activity.BaseActivity

inline fun <reified T : ViewModel> BaseActivity.getViewModel(viewModelFactory: ViewModelProvider.Factory): T {
    return androidx.lifecycle.ViewModelProviders.of(this, viewModelFactory)[T::class.java]
}

inline fun <reified T : ViewModel> Fragment.getViewModel(viewModelFactory: ViewModelProvider.Factory, fromActivity: Boolean = false): T {
    return when (fromActivity) {
        false -> ViewModelProviders.of(this, viewModelFactory)[T::class.java]
        else -> ViewModelProviders.of(activity!!, viewModelFactory)[T::class.java]
    }
}