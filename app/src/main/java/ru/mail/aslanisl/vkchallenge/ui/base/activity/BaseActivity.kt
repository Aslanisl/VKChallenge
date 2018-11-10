package ru.mail.aslanisl.vkchallenge.ui.base.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import ru.mail.aslanisl.vkchallenge.domain.App
import ru.mail.aslanisl.vkchallenge.domain.di.component.AppComponent
import ru.mail.aslanisl.vkchallenge.domain.di.factory.ViewModelFactory
import ru.mail.aslanisl.vkchallenge.extensions.getViewModel
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity() {

    @Inject
    protected lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectDI((application as App).appComponent)
    }

    abstract fun injectDI(appComponent: AppComponent)

    protected inline fun <reified T : ViewModel> initViewModel() = getViewModel<T>(viewModelFactory)
}