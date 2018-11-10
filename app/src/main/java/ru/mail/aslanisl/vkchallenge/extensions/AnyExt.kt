package ru.mail.aslanisl.vkchallenge.extensions

import androidx.annotation.StringRes
import ru.mail.aslanisl.vkchallenge.domain.App

fun getString(@StringRes stringRes: Int) = App.instance.getString(stringRes)