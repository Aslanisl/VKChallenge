package ru.mail.aslanisl.vkchallenge.domain.throwable

object ThrowableFactory {

    fun getThrowableType(throwable: Throwable): Throwable {
        return getThrowable(throwable.message)
    }

    fun getThrowableType(message: String?): Throwable {
        return getThrowable(message)
    }

    private fun getThrowable(message: String?): Throwable {
        message ?: return Throwable()
        return when {
            message.contains("Unable to resolve host") || message.contains("Timeout") -> {
                NoInternetThrowable()
            }
            else -> Throwable(message)
        }
    }
}