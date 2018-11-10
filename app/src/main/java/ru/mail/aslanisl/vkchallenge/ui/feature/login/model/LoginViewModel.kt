package ru.mail.aslanisl.vkchallenge.ui.feature.login.model

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKCallback
import com.vk.sdk.VKScope
import com.vk.sdk.VKSdk
import com.vk.sdk.api.VKError
import ru.mail.aslanisl.vkchallenge.data.base.UIData
import ru.mail.aslanisl.vkchallenge.ui.base.activity.BaseActivity
import ru.mail.aslanisl.vkchallenge.ui.base.model.BaseViewModel
import javax.inject.Inject

class LoginViewModel @Inject constructor() : BaseViewModel() {

    private val loginLiveData by lazy { MutableLiveData<UIData<Boolean>>() }

    private val vkCallback = object : VKCallback<VKAccessToken> {
        override fun onResult(res: VKAccessToken) {
            loginLiveData.postValue(UIData.success(true))
        }

        override fun onError(error: VKError) {
            loginLiveData.postValue(UIData.errorMessage(error.errorMessage))
        }
    }

    /**
     * U have to call holdActivityResult in onActivityResult
     * @see holdActivityResult
     */
    fun loginVk(activity: BaseActivity): LiveData<UIData<Boolean>> {
        loginLiveData.postValue(UIData.loading())
        VKSdk.login(activity, VKScope.WALL)
        return loginLiveData
    }

    fun holdActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        VKSdk.onActivityResult(requestCode, resultCode, data, vkCallback)
    }

    fun isUserLogin() = VKSdk.isLoggedIn()
}
