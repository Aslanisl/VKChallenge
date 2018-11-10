package ru.mail.aslanisl.vkchallenge.extensions

import android.view.View
import android.view.ViewGroup

fun ViewGroup.forChildEach(action: (View) -> Unit) {
    for (i in 0 until childCount) action.invoke(getChildAt(i))
}

fun ViewGroup.forChildEachReverse(action: (View, Int) -> Unit) {
    for (i in childCount - 1 downTo 0) action.invoke(getChildAt(i), i)
}