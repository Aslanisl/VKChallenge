package ru.mail.aslanisl.vkchallenge.ui.feature.login.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_login.*
import ru.mail.aslanisl.vkchallenge.R
import ru.mail.aslanisl.vkchallenge.data.base.UIData
import ru.mail.aslanisl.vkchallenge.domain.di.component.AppComponent
import ru.mail.aslanisl.vkchallenge.extensions.startActivity
import ru.mail.aslanisl.vkchallenge.extensions.toast
import ru.mail.aslanisl.vkchallenge.ui.base.activity.BaseActivity
import ru.mail.aslanisl.vkchallenge.ui.feature.login.model.LoginViewModel
import ru.mail.aslanisl.vkchallenge.ui.feature.wall.activity.WallActivity

class LoginActivity : BaseActivity() {

    companion object {
        fun startActivityAsTop(context: Context) {
            val intent = Intent(context, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            context.startActivity(intent)
        }
    }

    override fun injectDI(appComponent: AppComponent) = appComponent.inject(this)

    private val viewModel by lazy { initViewModel<LoginViewModel>() }
    private val loginObserver by lazy {
        Observer<UIData<Boolean>> {
            it ?: return@Observer
            initLoginData(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (viewModel.isUserLogin()) {
            startActivity<WallActivity>()
            finish()
            return
        }

        loginVkButton.setOnClickListener {
            viewModel.loginVk(this).observe(this, loginObserver)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.holdActivityResult(requestCode, resultCode, data)
    }

    private fun initLoginData(data: UIData<Boolean>) {
        loginVkButton.isEnabled = data.isLoading().not()
        loginVkButton.alpha = if (data.isLoading()) 0.4f else 1f

        when {
            data.isSuccess() -> {
                startActivity<WallActivity>()
                finish()
            }

            data.isError() -> toast(data.throwable?.message)
        }
    }
}
