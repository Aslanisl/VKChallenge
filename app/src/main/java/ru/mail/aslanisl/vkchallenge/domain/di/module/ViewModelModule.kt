package ru.mail.aslanisl.vkchallenge.domain.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.mail.aslanisl.vkchallenge.domain.di.factory.ViewModelFactory
import ru.mail.aslanisl.vkchallenge.domain.di.factory.ViewModelKey
import ru.mail.aslanisl.vkchallenge.ui.feature.login.model.LoginViewModel
import ru.mail.aslanisl.vkchallenge.ui.feature.wall.model.WallViewModel

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    internal abstract fun loginViewModel(viewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WallViewModel::class)
    internal abstract fun wallViewModel(viewModel: WallViewModel): ViewModel
}