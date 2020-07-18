package androidx.compose.dispatch

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.js.Date
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

actual val DefaultMonotonicFrameClock: MonotonicFrameClock = CompositionClock

object CompositionClock : MonotonicFrameClock {
    @OptIn(ExperimentalTime::class)
    override suspend fun <R> withFrameNanos(onFrame: (frameTimeNanos: Long) -> R): R =
        withContext(Dispatchers.Main) {
            onFrame(Date.now().milliseconds.toLongNanoseconds())
        }
}
