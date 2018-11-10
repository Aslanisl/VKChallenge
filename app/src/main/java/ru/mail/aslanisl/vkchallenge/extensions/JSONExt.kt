package ru.mail.aslanisl.vkchallenge.extensions

import org.json.JSONArray
import org.json.JSONObject

public inline fun JSONArray.forEach(action: (JSONObject) -> Unit) {
    for (i in 0 until this.length()) {
        action.invoke(this.getJSONObject(i))
    }
}