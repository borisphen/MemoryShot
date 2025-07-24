package com.borisphen.util

import kotlin.coroutines.cancellation.CancellationException

/**
 * @see <a href="https://github.com/arrow-kt/arrow/blob/main/arrow-libs/core/arrow-core/src/commonMain/kotlin/arrow/core/Either.kt">Arrow Either</a>
 */
sealed class Either<out L, out R> {

    data class Left<out L> constructor(val value: L) : Either<L, Nothing>() {

        override fun toString() = "Either.Left($value)"
    }

    data class Right<out R> constructor(val value: R) : Either<Nothing, R>() {

        override fun toString() = "Either.Right($value)"
    }

    inline fun <C> fold(ifLeft: (L) -> C, ifRight: (R) -> C): C = when (this) {
        is Left -> ifLeft(value)
        is Right -> ifRight(value)
    }

    companion object {

        inline fun <A> catch(block: () -> A): Either<Throwable, A> =
            try {
                block().right()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Error) {
                throw e
            } catch (t: Throwable) {
                t.left()
            }
    }
}

fun <L> L.left(): Either<L, Nothing> = Either.Left(this)

fun <R> R.right(): Either<Nothing, R> = Either.Right(this)

inline fun <L, R, T> Either<L, R>.map(transform: (R) -> T): Either<L, T> = when (this) {
    is Either.Right -> transform(value).right()
    is Either.Left -> this
}

//inline fun <L : Throwable, R, T> Either<L, R>.mapCatching(transform: (R) -> T): Either<L, T> =
//    when (this) {
//        is Either.Right -> Either.catch { transform(value) }.mapLeft { it as L }
//        is Either.Left -> this
//    }

inline fun <L, R, T> Either<L, R?>.mapNotNull(transform: (R) -> T): Either<L, T?> = when (this) {
    is Either.Right -> if (value != null) transform(value).right() else Either.Right(null)
    is Either.Left -> this
}

inline fun <L, R, ML, MR> Either<L, R>.foldMap(
    mapLeft: (L) -> ML,
    mapRight: (R) -> MR,
): Either<ML, MR> = when (this) {
    is Either.Left -> Either.Left(mapLeft(value))
    is Either.Right -> Either.Right(mapRight(value))
}

inline fun <LEFT, RIGHT> Either<LEFT, RIGHT>.onLeft(action: (LEFT) -> Unit): Either<LEFT, RIGHT> {
    if (this is Either.Left) action(value)
    return this
}

inline fun <LEFT, RIGHT> Either<LEFT, RIGHT>.onRight(action: (RIGHT) -> Unit): Either<LEFT, RIGHT> {
    if (this is Either.Right) action(value)
    return this
}

/**
 * Выполняет [action], если [Either.Right] и [Either.Right.value] != null.
 */
inline fun <LEFT, RIGHT> Either<LEFT, RIGHT?>.onRightNotNull(action: (RIGHT) -> Unit): Either<LEFT, RIGHT?> {
    if (this is Either.Right && this.value != null) action(value)
    return this
}

inline fun <L, R, T> Either<L, R>.mapLeft(transform: (L) -> T): Either<T, R> = when (this) {
    is Either.Right -> this
    is Either.Left -> transform(value).left()
}

fun <L : Throwable, R> Either<L, R>.getOrThrow(): R =
    when (this) {
        is Either.Right -> this.value
        is Either.Left -> throw this.value
    }

fun <L, R> Either<L, R>.getOrNull(): R? =
    when (this) {
        is Either.Left -> null
        is Either.Right -> value
    }

inline fun <L, R> Either<L, R>.getOrElse(onLeft: (L) -> R): R {
    return when (this) {
        is Either.Left -> onLeft(value)
        is Either.Right -> value
    }
}

fun <L, R> Either<L, R>.getLeftOrNull(): L? =
    when (this) {
        is Either.Left -> value
        is Either.Right -> null
    }

inline fun <L, R, T> Either<L, R>.flatMap(
    transform: (R) -> Either<L, T>,
): Either<L, T> =
    when (this) {
        is Either.Right -> transform(this.value)
        is Either.Left -> this
    }

inline fun <L, R, T> Either<L, R>.flatMapLeft(
    transform: (L) -> Either<T, R>,
): Either<T, R> =
    when (this) {
        is Either.Right -> this
        is Either.Left -> transform(this.value)
    }

fun <L, R> Either<L, R>.isLeft(): Boolean = this is Either.Left

fun <L, R> Either<L, R>.isRight(): Boolean = this is Either.Right
