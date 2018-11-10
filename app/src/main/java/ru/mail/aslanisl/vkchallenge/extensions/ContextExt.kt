package ru.mail.aslanisl.vkchallenge.extensions

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.annotation.StringRes

inline fun <reified T> Context.startActivity() {
    startActivity(Intent(this, T::class.java))
}

fun Context.toast(message: String?, duration: Int = Toast.LENGTH_LONG) {
    message?.let {
        Toast.makeText(this, it, duration).show()
    }
}

fun Context.toast(@StringRes messageInt: Int?, duration: Int = Toast.LENGTH_LONG) {
    messageInt?.let {
        Toast.makeText(this, getString(messageInt), duration).show()
    }
}