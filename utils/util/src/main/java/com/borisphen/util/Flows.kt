package com.borisphen.util

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.toKotlinDuration

fun <T> lazyFlow(
    mode: LazyThreadSafetyMode = LazyThreadSafetyMode.NONE,
    initializer: () -> Flow<T>,
): Lazy<Flow<T>> = lazy(mode, initializer)

fun <T> lazyStateFlow(
    mode: LazyThreadSafetyMode = LazyThreadSafetyMode.NONE,
    initializer: () -> StateFlow<T>,
): Lazy<StateFlow<T>> = lazy(mode, initializer)

inline fun <T> mutableStateFlow(
    mode: LazyThreadSafetyMode = LazyThreadSafetyMode.NONE,
    crossinline initialState: () -> T,
): Lazy<MutableStateFlow<T>> = lazy(mode) {
    MutableStateFlow(initialState())
}

fun <T> mutableSharedFlow(
    replay: Int = 1,
    mode: LazyThreadSafetyMode = LazyThreadSafetyMode.NONE,
): Lazy<MutableSharedFlow<T>> = lazy(mode) {
    require(replay > 0) { "replay must be positive" }
    MutableSharedFlow(
        replay = replay,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
}

fun <T> mutableBufferedSharedFlow(bufferCapacity: Int = 1, replay: Int = 0) =
    MutableSharedFlow<T>(
        extraBufferCapacity = bufferCapacity,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
        replay = replay,
    )

fun <A, B : Any> Flow<A>.withLatestFrom(other: Flow<B>): Flow<Pair<A, B>> =
    withLatestFrom(other) { a, b -> a to b }

// Источник: https://github.com/Kotlin/kotlinx.coroutines/issues/1498#issue-488519994
fun <A, B : Any, R> Flow<A>.withLatestFrom(
    other: Flow<B>,
    transform: suspend (A, B) -> R
): Flow<R> = flow {
    coroutineScope {
        val latestB = AtomicReference<B?>()
        val outerScope = this
        launch {
            try {
                other.collect { latestB.set(it) }
            } catch (e: CancellationException) {
                outerScope.cancel(e) // cancel outer scope on cancellation exception, too
            }
        }
        collect { a: A ->
            latestB.get()?.let { b -> emit(transform(a, b)) }
        }
    }
}

@OptIn(FlowPreview::class)
fun <T> SharedFlow<T>.debounce(debounce: java.time.Duration): Flow<T> {
    return debounce(debounce.toKotlinDuration())
}

fun <T> Flow<T>?.orEmpty() = this ?: emptyFlow()

fun <T1, R> StateFlow<T1>.mapState(transform: (a: T1) -> R): StateFlow<R> {
    return DerivedStateFlow(
        getValue = { transform(this.value) },
        flow = this.map { a -> transform(a) },
    )
}

/**
 * Does not produce the same value in a raw, so respect "distinct until changed emissions"
 * */
class DerivedStateFlow<T>(
    private val getValue: () -> T,
    private val flow: Flow<T>,
) : StateFlow<T> {

    override val replayCache: List<T>
        get() = listOf(value)

    override val value: T
        get() = getValue()

    @InternalCoroutinesApi
    override suspend fun collect(collector: FlowCollector<T>): Nothing {
        coroutineScope { flow.distinctUntilChanged().stateIn(this).collect(collector) }
    }
}
