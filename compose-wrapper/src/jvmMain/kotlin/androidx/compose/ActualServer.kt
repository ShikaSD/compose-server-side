package androidx.compose

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit

actual abstract class EmbeddingUIContext

actual class Looper

internal actual object LooperWrapper {
    private val mainLooper = Looper()
    actual fun getMainLooper(): Looper = mainLooper
}

internal actual class Handler actual constructor(looper: Looper) {
    actual fun post(block: () -> Unit): Boolean {
        composeThreadExecutor.execute(block)
        return true
    }
}

private val threadFactory = object : ThreadFactory {
    @Volatile
    var currentThread: Thread? = null

    override fun newThread(r: Runnable): Thread =
        Thread(r, "compose-runner-thread")
            .also { currentThread = it }
}
val composeThreadExecutor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(threadFactory)
private val composeThreadDispatcher = composeThreadExecutor.asCoroutineDispatcher()

internal actual fun isMainThread(): Boolean =
    Thread.currentThread() == threadFactory.currentThread

internal actual fun mainThreadCompositionDispatcher(): CoroutineDispatcher =
    composeThreadDispatcher

internal actual fun mainThreadCompositionFrameClock(): CompositionFrameClock =
    CompositionClock

private object CompositionClock : CompositionFrameClock {
    override suspend fun <R> withFrameNanos(onFrame: (frameTimeNanos: Long) -> R): R =
        withContext(composeThreadDispatcher) {
            onFrame(System.nanoTime())
        }
}

internal actual object Choreographer {
    private val cancelled = mutableSetOf<ChoreographerFrameCallback>()

    actual fun postFrameCallback(callback: ChoreographerFrameCallback) {
        composeThreadExecutor.execute {
            if (callback !in cancelled) {
                callback.doFrame(System.nanoTime())
            } else {
                cancelled.remove(callback)
            }
        }
    }
    actual fun postFrameCallbackDelayed(delayMillis: Long, callback: ChoreographerFrameCallback) {
        composeThreadExecutor.schedule(
            { callback.doFrame(System.nanoTime()) },
            delayMillis,
            TimeUnit.MILLISECONDS
        )
    }
    actual fun removeFrameCallback(callback: ChoreographerFrameCallback) {
        cancelled += callback
    }
}

actual interface ChoreographerFrameCallback {
    actual fun doFrame(frameTimeNanos: Long)
}

internal actual object Trace {
    actual fun beginSection(name: String) {
        // Do nothing.
    }

    actual fun endSection() {
        // Do nothing.
    }
}

internal val keyInfo = mutableMapOf<Int, String>()

private fun findSourceKey(key: Any): Int? =
    when (key) {
        is Int -> key
        is JoinedKey -> {
            key.left?.let { findSourceKey(it) } ?: key.right?.let { findSourceKey(it) }
        }
        else -> null
    }

internal actual fun recordSourceKeyInfo(key: Any) {
    val sk = findSourceKey(key)
    sk?.let {
        keyInfo.getOrPut(sk, {
            val stack = Thread.currentThread().stackTrace
            // On Android the frames looks like:
            //  0: getThreadStackTrace() (native method)
            //  1: getStackTrace()
            //  2: recordSourceKey()
            //  3: start()
            //  4: startGroup() or startNode()
            //  5: non-inline call/emit?
            //  5 or 6: <calling method>
            // On a desktop VM this looks like:
            //  0: getStackTrace()
            //  1: recordSourceKey()
            //  2: start()
            //  3: startGroup() or startNode()
            //  4: non-inline call/emit?
            //  4 or 5: <calling method>
            //
            // Quite weird, to be corrected later (maybe copy from fixed desktop impl)
            val frameNumber = stack[4].let {
                if (it.methodName == "startRestartGroup") 5 else 4
            }
            val frame = stack[frameNumber].let {
                if (it.methodName == "call" || it.methodName == "emit")
                    stack[frameNumber + 1]
                else
                    stack[frameNumber]
            }
            "${frame.className}.${frame.methodName} (${frame.fileName}:${frame.lineNumber})"
        })
    }
}

actual fun keySourceInfoOf(key: Any): String? = keyInfo[key]

actual fun resetSourceInfo() {
    keyInfo.clear()
}

actual annotation class MainThread()
actual annotation class CheckResult(actual val suggest: String)
