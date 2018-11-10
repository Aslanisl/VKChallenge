package ru.mail.aslanisl.vkchallenge.domain.di.component

import dagger.Component
import ru.mail.aslanisl.vkchallenge.domain.di.module.AppModule
import ru.mail.aslanisl.vkchallenge.ui.feature.login.activity.LoginActivity
import ru.mail.aslanisl.vkchallenge.ui.feature.wall.activity.WallActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(activity: LoginActivity)
    fun inject(activity: WallActivity)
}