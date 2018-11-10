package ru.mail.aslanisl.vkchallenge.ui.feature.wall.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import ru.mail.aslanisl.vkchallenge.data.base.UIData
import ru.mail.aslanisl.vkchallenge.data.model.VKWallModel
import ru.mail.aslanisl.vkchallenge.domain.repository.WallRepository
import ru.mail.aslanisl.vkchallenge.ui.base.model.BaseViewModel
import javax.inject.Inject

class WallViewModel
@Inject constructor(private val wallRepository: WallRepository) : BaseViewModel() {

    private val wallLiveData by lazy { MediatorLiveData<UIData<List<VKWallModel>>>() }

    fun loadWalls(): LiveData<UIData<List<VKWallModel>>> {
        wallLiveData.postValue(UIData.loading())
        val source = wallRepository.loadWalls()
        wallLiveData.addSource(source) {
            wallLiveData.removeSource(source)
            wallLiveData.postValue(it)
        }
        return wallLiveData
    }
}