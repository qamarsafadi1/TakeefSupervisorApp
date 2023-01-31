package com.selsela.takeefapp.utils.retrofit.model


data class Resource<out T>(
    val status: Status,
    val data: T?,
    val message: String?,
    val errors: List<Errors>?

) {
    companion object {
        fun <T> success(data: T?, message: String? = ""): Resource<T> =
            Resource(status = Status.SUCCESS, data = data, message = message, errors = null)

        fun <T> error(
            data: T?,
            message: String?,
            errors: List<Errors>?
        ): Resource<T> =
            Resource(status = Status.ERROR, data = data, message = message ?: "", errors = errors)

        fun <T> loading(data: T?): Resource<T> =
            Resource(status = Status.LOADING, data = data, message = null, errors = null)
    }
}
