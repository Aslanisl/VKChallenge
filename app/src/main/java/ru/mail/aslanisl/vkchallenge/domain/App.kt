package ru.mail.aslanisl.vkchallenge.domain

import android.app.Application
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKAccessTokenTracker
import com.vk.sdk.VKSdk
import ru.mail.aslanisl.vkchallenge.domain.di.component.DaggerAppComponent
import ru.mail.aslanisl.vkchallenge.domain.di.module.AppModule
import ru.mail.aslanisl.vkchallenge.ui.feature.login.activity.LoginActivity

class App : Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    val appComponent by lazy {
        DaggerAppComponent
            .builder()
            .appModule(AppModule(this))
            .build() as DaggerAppComponent
    }

    private val vkAccessTokenTracker = object : VKAccessTokenTracker() {
        override fun onVKAccessTokenChanged(oldToken: VKAccessToken?, newToken: VKAccessToken?) {
            LoginActivity.startActivityAsTop(this@App)
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        vkAccessTokenTracker.startTracking()
        VKSdk.initialize(this)
    }
}