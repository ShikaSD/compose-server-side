package androidx.compose

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.js.Date
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

actual fun EmbeddingContext(): EmbeddingContext = EmbeddingBrowserContext

object EmbeddingBrowserContext : EmbeddingContext, CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.Main

    override fun isMainThread(): Boolean = true

    override fun mainThreadCompositionContext(): CoroutineContext =
        coroutineContext

    override fun postOnMainThread(block: () -> Unit) {
        launch { block() }
    }

    private val cancelled = mutableSetOf<ChoreographerFrameCallback>()

    @OptIn(ExperimentalTime::class)
    override fun postFrameCallback(callback: ChoreographerFrameCallback) {
        launch {
            if (callback !in cancelled) {
                callback.doFrame(Date.now().milliseconds.toLongNanoseconds())
            } else {
                cancelled.remove(callback)
            }
        }
    }

    override fun cancelFrameCallback(callback: ChoreographerFrameCallback) {
        cancelled += callback
    }
}

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
            // TODO
            ""
        })
    }
}

actual fun keySourceInfoOf(key: Any): String? = keyInfo[key]

actual fun resetSourceInfo() {
    keyInfo.clear()
}

actual annotation class MainThread()
actual annotation class CheckResult(actual val suggest: String)
