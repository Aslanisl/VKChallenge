package ru.mail.aslanisl.vkchallenge.domain.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vk.sdk.api.VKApiConst
import com.vk.sdk.api.VKError
import com.vk.sdk.api.VKParameters
import com.vk.sdk.api.VKRequest
import com.vk.sdk.api.VKResponse
import ru.mail.aslanisl.vkchallenge.R
import ru.mail.aslanisl.vkchallenge.data.base.UIData
import ru.mail.aslanisl.vkchallenge.data.model.VKWallModel
import ru.mail.aslanisl.vkchallenge.data.response.VKWallResponse
import ru.mail.aslanisl.vkchallenge.domain.App
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WallRepository @Inject constructor() {

    private var lastWallResponse: VKWallResponse? = null

    fun loadWalls(): LiveData<UIData<List<VKWallModel>>> {
        val wallLiveData = MutableLiveData<UIData<List<VKWallModel>>>()
        val startFrom = lastWallResponse?.nextFrom
        val request = VKRequest(
            "newsfeed.getDiscoverForContestant",
            VKParameters.from(VKApiConst.EXTENDED, 1)
        )
        if (startFrom.isNullOrEmpty().not()) request.addExtraParameter("start_from", startFrom)
        request.executeWithListener(object : VKRequest.VKRequestListener() {
            override fun onComplete(response: VKResponse?) {
                super.onComplete(response)
                lastWallResponse = VKWallResponse().parse(response?.json)
                val walls = lastWallResponse?.items
                walls?.forEach { wall ->
                    val sourceId = wall.sourceId
                    if (sourceId > 0){
                        val profile = lastWallResponse?.profiles?.firstOrNull { it.id == wall.sourceId }
                        wall.profile = profile
                    } else {
                        val id = Math.abs(sourceId)
                        val group = lastWallResponse?.groups?.firstOrNull { it.id == id }
                        wall.group = group
                    }
                }
                wallLiveData.postValue(UIData.success(walls))
            }

            override fun onError(error: VKError?) {
                super.onError(error)
                wallLiveData.postValue(UIData.errorMessage(error?.errorMessage))
            }
        })
        return wallLiveData
    }
}