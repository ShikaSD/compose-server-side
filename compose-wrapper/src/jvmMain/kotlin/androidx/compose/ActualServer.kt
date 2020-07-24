package androidx.compose

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ThreadFactory
import kotlin.coroutines.CoroutineContext

actual fun EmbeddingContext(): EmbeddingContext = EmbeddingServerContext

object EmbeddingServerContext : EmbeddingContext, CoroutineScope {
    override val coroutineContext: CoroutineContext = composeThreadDispatcher

    override fun isMainThread(): Boolean =
        Thread.currentThread() == threadFactory.currentThread

    override fun mainThreadCompositionContext(): CoroutineContext =
        coroutineContext

    override fun postOnMainThread(block: () -> Unit) {
        launch { block() }
    }

    private val cancelled = mutableSetOf<ChoreographerFrameCallback>()

    override fun postFrameCallback(callback: ChoreographerFrameCallback) {
        composeThreadExecutor.execute {
            if (callback !in cancelled) {
                callback.doFrame(System.nanoTime())
            } else {
                cancelled.remove(callback)
            }
        }
    }

    override fun cancelFrameCallback(callback: ChoreographerFrameCallback) {
        cancelled += callback
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
val composeThreadDispatcher = composeThreadExecutor.asCoroutineDispatcher()

actual interface ChoreographerFrameCallback {
    actual fun doFrame(frameTimeNanos: Long)
}

val traceLoggers = mutableListOf<TraceLogger>()

interface TraceLogger {
    fun start(name: String)
    fun end()
}

internal actual object Trace {
    actual fun beginSection(name: String) {
        traceLoggers.forEach { it.start(name) }
    }

    actual fun endSection() {
        traceLoggers.forEach { it.end() }
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
