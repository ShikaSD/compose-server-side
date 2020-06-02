package androidx.compose

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope

@OptIn(InternalComposeApi::class)
internal class ServerRecomposer : Recomposer() {

    private var frameScheduled = false
    override val effectCoroutineScope: CoroutineScope = GlobalScope
    override val compositionFrameClock: CompositionFrameClock = object : CompositionFrameClock {
        override suspend fun <R> awaitFrameNanos(onFrame: (frameTimeNanos: Long) -> R): R =
            onFrame(0)
    }

    inner class Callback : Runnable {
        @Volatile var cancelled: Boolean = false

        override fun run() {
            if (cancelled) return
            frameScheduled = false
            dispatchRecomposes()
        }
    }

    private val frameCallback = Callback()

    init {
        composeThreadExecutor.execute(Callback())
    }

    override fun scheduleChangesDispatch() {
        if (!frameScheduled) {
            frameScheduled = true
            composeThreadExecutor.execute(Callback())
        }
    }

    override fun hasPendingChanges(): Boolean = frameScheduled

    override fun recomposeSync() {
        if (frameScheduled) {
            frameCallback.cancelled = true
            frameCallback.run()
        }
    }
}
