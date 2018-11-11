package ru.mail.aslanisl.vkchallenge.data.model

import com.vk.sdk.api.model.VKApiCommunity
import com.vk.sdk.api.model.VKApiUser
import com.vk.sdk.api.model.VKAttachments
import org.json.JSONException
import org.json.JSONObject

class VKWallModel {

    var postId: Int = 0
        private set
    var text: String = ""
        private set
    var sourceId: Int = 0
        private set
    var date: Long = 0
        private set
    var attachments: VKAttachments = VKAttachments()
        private set
    var profile: VKApiUser? = null
    var group: VKApiCommunity? = null
    var ownerId: Int = 0

    fun parse(source: JSONObject?): VKWallModel {
        source ?: return this
        try {
            postId = source.optInt("post_id")
            text = source.optString("text")
            sourceId = source.optInt("source_id")
            attachments.fill(source.optJSONArray("attachments"))
            date = source.optLong("date")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return this
    }
}