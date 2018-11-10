package ru.mail.aslanisl.vkchallenge.utils

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions

private const val TIMEOUT_MILLISECONDS = 30 * 1000

@GlideModule
open class GlideModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val requestOptions = RequestOptions().timeout(TIMEOUT_MILLISECONDS)
        builder.setDefaultRequestOptions(requestOptions)
    }
}