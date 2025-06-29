package com.borisphen.util

import retrofit2.HttpException
import retrofit2.Response

suspend fun <T> apiCall(block: suspend () -> Response<T>): Either<Throwable, T> = Either.catch {
    block().resultOrThrowException()
}

fun <T> Response<T>.resultOrThrowException(): T {
    return if (!this.isSuccessful) {
        throw EcomSdkHttpException(this)
    } else {
        this.body() ?: throw EcomSdkHttpException(this)
    }
}

class EcomSdkHttpException(val response: Response<*>) :
    Exception("Http code=${response.code()}", HttpException(response)) {

    val code = response.code()
}

suspend fun <T : Any, R> apiCallFold(
    query: suspend () -> T,
    onSuccess: (T) -> R,
    onFailure: (Throwable) -> R,
): R = Either.catch { query() }
    .fold(ifRight = onSuccess, ifLeft = onFailure)