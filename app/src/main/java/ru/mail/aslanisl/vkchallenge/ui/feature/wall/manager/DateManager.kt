package ru.mail.aslanisl.vkchallenge.ui.feature.wall.manager

import ru.mail.aslanisl.vkchallenge.R
import ru.mail.aslanisl.vkchallenge.extensions.getString
import java.text.SimpleDateFormat
import java.util.*

object DateManager {

    private val timeString by lazy { getString(R.string.time_in) }

    /**
     * @param dateStamp in seconds
     */
    fun formatDateStamp(dateStamp: Long): String {
        val millis = dateStamp * 1000
        val date = Calendar.getInstance().apply { timeInMillis = millis }
        val dayString = getDayString(date)
        val hourString = getHourString(date)
        return timeString.format(dayString, hourString)
    }

    private fun getDayString(calendar: Calendar): String {
        val currentCalendar = Calendar.getInstance()
        return when {
            currentCalendar[Calendar.DATE] == calendar[Calendar.DATE] -> getString(R.string.today)
            currentCalendar[Calendar.DATE] - calendar[Calendar.DATE] == 1 -> getString(R.string.yesterday)
            else -> SimpleDateFormat("dd MMM", Locale.getDefault()).format(calendar.time)
        }
    }

    private fun getHourString(calendar: Calendar): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
    }
}