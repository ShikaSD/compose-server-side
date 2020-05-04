package androidx.compose

internal class ServerRecomposer : Recomposer() {

    private var frameScheduled = false

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
