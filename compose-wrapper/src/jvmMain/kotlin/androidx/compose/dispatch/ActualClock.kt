package androidx.compose.dispatch

import androidx.compose.composeThreadDispatcher
import kotlinx.coroutines.withContext

actual val DefaultMonotonicFrameClock: MonotonicFrameClock = CompositionClock

object CompositionClock : MonotonicFrameClock {
    override suspend fun <R> withFrameNanos(onFrame: (frameTimeNanos: Long) -> R): R =
        withContext(composeThreadDispatcher) {
            onFrame(System.nanoTime())
        }
}
