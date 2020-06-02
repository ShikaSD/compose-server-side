package androidx.compose

import kotlinx.coroutines.*
import java.lang.Runnable

@OptIn(InternalComposeApi::class)
internal class ServerRecomposer : Recomposer() {

    private var frameScheduled = false
    private val composeDispatcher = composeThreadExecutor.asCoroutineDispatcher()

    override val effectCoroutineScope: CoroutineScope = CoroutineScope(
        SupervisorJob() + composeDispatcher
    )
    override val compositionFrameClock: CompositionFrameClock =
        object : CompositionFrameClock {
            override suspend fun <R> awaitFrameNanos(onFrame: (frameTimeNanos: Long) -> R): R =
                withContext(composeDispatcher) {
                    onFrame(System.nanoTime())
                } }

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
