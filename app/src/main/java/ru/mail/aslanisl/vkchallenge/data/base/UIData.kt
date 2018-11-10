package ru.mail.aslanisl.vkchallenge.data.base

import ru.mail.aslanisl.vkchallenge.domain.throwable.ThrowableFactory

class UIData<out T> private constructor(
    val status: UIDataStatus,
    val body: T? = null,
    var throwable: Throwable? = null,
    val code: Int? = null
) {

    companion object {
        fun loading() = UIData(UIDataStatus.LOADING, null)

        fun <T> loading(cache: T?) = UIData(UIDataStatus.LOADING, cache)

        fun <T> success(body: T?, throwable: Throwable? = null) =
            UIData(UIDataStatus.SUCCESS, body, throwable)

        fun errorThrowable(throwable: Throwable? = null, code: Int? = null): UIData<Nothing> {
            return UIData(
                UIDataStatus.FAILURE,
                null,
                throwable = throwable,
                code = code
            )
        }

        fun errorMessage(errorMessage: String? = null, code: Int? = null): UIData<Nothing> {
            return UIData(
                status = UIDataStatus.FAILURE,
                body = null,
                throwable = ThrowableFactory.getThrowableType(errorMessage),
                code = code
            )
        }
    }

    fun isSuccess() = status == UIDataStatus.SUCCESS

    fun isLoading() = status == UIDataStatus.LOADING

    fun isError() = status == UIDataStatus.FAILURE
}