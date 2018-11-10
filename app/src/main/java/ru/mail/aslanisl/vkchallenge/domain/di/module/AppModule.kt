package ru.mail.aslanisl.vkchallenge.domain.di.module

import android.content.Context
import dagger.Module
import dagger.Provides

@Module(includes = [ViewModelModule::class])
class AppModule(val context: Context) {

    @Provides
    fun provideContext() = context
}