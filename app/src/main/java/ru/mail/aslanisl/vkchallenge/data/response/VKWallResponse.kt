package ru.mail.aslanisl.vkchallenge.data.response

import com.vk.sdk.api.model.VKApiCommunity
import com.vk.sdk.api.model.VKApiUser
import org.json.JSONException
import org.json.JSONObject
import ru.mail.aslanisl.vkchallenge.data.model.VKWallModel
import ru.mail.aslanisl.vkchallenge.extensions.forEach

class VKWallResponse {

    val items: MutableList<VKWallModel> = mutableListOf()
    val profiles: MutableList<VKApiUser> = mutableListOf()
    val groups: MutableList<VKApiCommunity> = mutableListOf()
    var nextFrom: String = ""

    fun parse(source: JSONObject?): VKWallResponse {
        source ?: return this
        val response = source.optJSONObject("response") ?: return this
        try {
            response.optJSONArray("items")?.forEach {
                items += VKWallModel().parse(it)
            }
            response.optJSONArray("profiles")?.forEach {
                profiles += VKApiUser().parse(it)
            }
            response.optJSONArray("groups")?.forEach {
                groups += VKApiCommunity().parse(it)
            }
            nextFrom = response.optString("next_from")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return this
    }
}