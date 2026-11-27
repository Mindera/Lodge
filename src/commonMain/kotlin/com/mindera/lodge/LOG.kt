package com.mindera.lodge

import com.mindera.lodge.LOG.SEVERITY.DEBUG
import com.mindera.lodge.LOG.SEVERITY.ERROR
import com.mindera.lodge.LOG.SEVERITY.FATAL
import com.mindera.lodge.LOG.SEVERITY.INFO
import com.mindera.lodge.LOG.SEVERITY.VERBOSE
import com.mindera.lodge.LOG.SEVERITY.WARN
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * LOG static class. It is used to abstract the LOG and have multiple possible implementations
 * It is used also to serve as static references for logging methods to be called.
 */
object LOG {

    /**
     * Registered appenders, each with the [Appender.minLogLevel] it was added with.
     * Only accessed from the worker coroutine.
     */
    private val appenders: MutableMap<Appender, SEVERITY> = mutableMapOf()

    /**
     * How many appenders accept each severity, packed as one [LEVEL_BITS]-bit counter per
     * [SEVERITY]. It lets [log] drop a message on the calling thread when nothing would accept it.
     *
     * A counter goes up as soon as [add] is called and only comes down on the worker, once the
     * appender is really gone. So it can overcount for a moment, but it never hides a level
     * that an appender, registered or still queued, is waiting for.
     */
    private val accepted = MutableStateFlow(0L)

    /**
     * Work queue
     */
    private val tasks = Channel<() -> Unit>(UNLIMITED)

    /**
     * A dedicated coroutine that pulls lambdas out of `tasks` and executes
     * them one-by-one, preserving the exact order in which they were queued.
     */
    @Suppress("unused")
    private val scope = CoroutineScope(SupervisorJob() + Default).apply {
        launch {
            for (task in tasks) {
                runCatching { task() }
            }
        }
    }

    /**
     * Enable log appender. No-op if an appender with the same [Appender.loggerId] is already registered.
     *
     * @param appender Log appender to enable
     */
    fun add(appender: Appender) {
        add(listOf(appender))
    }

    /**
     * Enable log appenders. Skips any appender whose [Appender.loggerId] is already registered.
     *
     * @param appenders Log appenders to enable
     */
    fun add(appenders: List<Appender>) {
        val candidates = appenders.map { it to it.minLogLevel }
        val counters = candidates.sumOf { (_, level) -> level.counter }
        accepted.update { it + counters }
        tasks.trySend {
            candidates.forEach { (candidate, level) ->
                if (this.appenders.keys.none { it.loggerId == candidate.loggerId }) {
                    this.appenders[candidate] = level
                } else {
                    accepted.update { it - level.counter }
                    log("LOG", WARN, null) { "Appender '${candidate.loggerId}' discarded: an appender with that id is already registered." }
                }
            }
        }
    }

    /**
     * Remove log appenders
     *
     * @param id Log id of the loggers to be removed
     */
    fun remove(id: String) {
        tasks.trySend { removeIf { id == it.loggerId } }
    }

    /**
     * Remove log appenders
     *
     * @param ids Log ids of each of the loggers enabled by the order sent
     */
    fun remove(ids: Set<String>) {
        tasks.trySend { removeIf { ids.contains(it.loggerId) } }
    }

    private inline fun removeIf(predicate: (Appender) -> Boolean) {
        val entries = appenders.entries.iterator()
        for ((appender, level) in entries) {
            if (predicate(appender)) {
                entries.remove()
                accepted.update { it - level.counter }
            }
        }
    }

    /**
     * Suspends until every log, add and remove queued before this call has been processed.
     * Call it before the process is expected to end, so queued logs are not lost.
     */
    suspend fun flush() {
        val done = CompletableDeferred<Unit>()
        tasks.trySend { done.complete(Unit) }
        done.await()
    }

    /**
     * Log with a VERBOSE level
     *
     * @param tag  Used to identify the source of a log message.
     * @param text The message you would like logged.
     */
    fun v(tag: String, text: String) {
        log(tag, VERBOSE, null) { text }
    }

    /**
     * Log with a VERBOSE level
     *
     * @param tag  Used to identify the source of a log message.
     * @param message Lambda that returns the message to be logged.
     */
    fun v(tag: String, message: () -> String) {
        log(tag, VERBOSE, null, message)
    }

    /**
     * Log with a VERBOSE level
     *
     * @param tag  Used to identify the source of a log message.
     * @param t    Throwable
     * @param text The message you would like logged.
     */
    fun v(tag: String, t: Throwable, text: String) {
        log(tag, VERBOSE, t) { text }
    }

    /**
     * Log with a VERBOSE level
     *
     * @param tag  Used to identify the source of a log message.
     * @param t    Throwable
     * @param message Lambda that returns the message to be logged.
     */
    fun v(tag: String, t: Throwable, message: () -> String) {
        log(tag, VERBOSE, t, message)
    }

    /**
     * Log with a DEBUG level
     *
     * @param tag  Used to identify the source of a log message.
     * @param text The message you would like logged.
     */
    fun d(tag: String, text: String) {
        log(tag, DEBUG, null) { text }
    }

    /**
     * Log with a DEBUG level
     *
     * @param tag  Used to identify the source of a log message.
     * @param message Lambda that returns the message to be logged.
     */
    fun d(tag: String, message: () -> String) {
        log(tag, DEBUG, null, message)
    }

    /**
     * Log with a DEBUG level
     *
     * @param tag  Used to identify the source of a log message.
     * @param t    Throwable
     * @param text The message you would like logged.
     */
    fun d(tag: String, t: Throwable, text: String) {
        log(tag, DEBUG, t) { text }
    }

    /**
     * Log with a DEBUG level
     *
     * @param tag  Used to identify the source of a log message.
     * @param t    Throwable
     * @param message Lambda that returns the message to be logged.
     */
    fun d(tag: String, t: Throwable, message: () -> String) {
        log(tag, DEBUG, t, message)
    }

    /**
     * Log with a INFO level
     *
     * @param tag  Used to identify the source of a log message.
     * @param text The message you would like logged.
     */
    fun i(tag: String, text: String) {
        log(tag, INFO, null) { text }
    }

    /**
     * Log with a INFO level
     *
     * @param tag  Used to identify the source of a log message.
     * @param message Lambda that returns the message to be logged.
     */
    fun i(tag: String, message: () -> String) {
        log(tag, INFO, null, message)
    }

    /**
     * Log with a INFO level
     *
     * @param tag  Used to identify the source of a log message.
     * @param t    Throwable
     * @param text The message you would like logged.
     */
    fun i(tag: String, t: Throwable, text: String) {
        log(tag, INFO, t) { text }
    }

    /**
     * Log with a INFO level
     *
     * @param tag  Used to identify the source of a log message.
     * @param t    Throwable
     * @param message Lambda that returns the message to be logged.
     */
    fun i(tag: String, t: Throwable, message: () -> String) {
        log(tag, INFO, t, message)
    }

    /**
     * Log with a WARN level
     *
     * @param tag  Used to identify the source of a log message.
     * @param text The message you would like logged.
     */
    fun w(tag: String, text: String) {
        log(tag, WARN, null) { text }
    }

    /**
     * Log with a WARN level
     *
     * @param tag  Used to identify the source of a log message.
     * @param message Lambda that returns the message to be logged.
     */
    fun w(tag: String, message: () -> String) {
        log(tag, WARN, null, message)
    }

    /**
     * Log with a WARN level
     *
     * @param tag  Used to identify the source of a log message.
     * @param t    Throwable
     * @param text The message you would like logged.
     */
    fun w(tag: String, t: Throwable, text: String) {
        log(tag, WARN, t) { text }
    }

    /**
     * Log with a WARN level
     *
     * @param tag  Used to identify the source of a log message.
     * @param t    Throwable
     * @param message Lambda that returns the message to be logged.
     */
    fun w(tag: String, t: Throwable, message: () -> String) {
        log(tag, WARN, t, message)
    }

    /**
     * Log with a ERROR level
     *
     * @param tag  Used to identify the source of a log message.
     * @param text The message you would like logged.
     */
    fun e(tag: String, text: String) {
        log(tag, ERROR, null) { text }
    }

    /**
     * Log with a ERROR level
     *
     * @param tag  Used to identify the source of a log message.
     * @param message Lambda that returns the message to be logged.
     */
    fun e(tag: String, message: () -> String) {
        log(tag, ERROR, null, message)
    }

    /**
     * Log with a ERROR level
     *
     * @param tag  Used to identify the source of a log message.
     * @param t    Throwable
     * @param text The message you would like logged.
     */
    fun e(tag: String, t: Throwable, text: String) {
        log(tag, ERROR, t) { text }
    }

    /**
     * Log with a ERROR level
     *
     * @param tag  Used to identify the source of a log message.
     * @param t    Throwable
     * @param message Lambda that returns the message to be logged.
     */
    fun e(tag: String, t: Throwable, message: () -> String) {
        log(tag, ERROR, t, message)
    }

    /**
     * Log a What a Terrible Failure: Report an exception that should never happen.
     *
     * @param tag  Used to identify the source of a log message.
     * @param text The message you would like logged.
     */
    fun wtf(tag: String, text: String) {
        log(tag, FATAL, null) { text }
    }

    /**
     * Log a What a Terrible Failure: Report an exception that should never happen.
     *
     * @param tag  Used to identify the source of a log message.
     * @param message Lambda that returns the message to be logged.
     */
    fun wtf(tag: String, message: () -> String) {
        log(tag, FATAL, null, message)
    }

    /**
     * Log a What a Terrible Failure: Report an exception that should never happen.
     *
     * @param tag  Used to identify the source of a log message.
     * @param t    Throwable
     * @param text The message you would like logged.
     */
    fun wtf(tag: String, t: Throwable, text: String) {
        log(tag, FATAL, t) { text }
    }

    /**
     * Log a What a Terrible Failure: Report an exception that should never happen.
     *
     * @param tag  Used to identify the source of a log message.
     * @param t    Throwable
     * @param message Lambda that returns the message to be logged.
     */
    fun wtf(tag: String, t: Throwable, message: () -> String) {
        log(tag, FATAL, t, message)
    }

    private fun log(
        tag: String,
        severity: SEVERITY,
        t: Throwable?,
        message: () -> String,
    ) {
        if (accepted.value and severity.acceptedMask == 0L) return
        val originalThread = threadName
        tasks.trySend {
            val eligible = appenders.filterValues { it <= severity }.keys
            if (eligible.isNotEmpty()) {
                val log = "[T#$originalThread] | ${message()}"
                eligible.forEach {
                    runCatching { it.log(severity, tag, t, log) }
                }
            }
        }
    }

    /**
     * One appender in this severity's counter of [accepted].
     */
    private val SEVERITY.counter: Long get() = 1L shl (ordinal * LEVEL_BITS)

    /**
     * Covers the counters of every appender level that accepts this severity: its own and all below.
     */
    private val SEVERITY.acceptedMask: Long get() = (1L shl ((ordinal + 1) * LEVEL_BITS)) - 1

    /**
     * Width of each counter in [accepted]: room for 1023 appenders per level.
     */
    private const val LEVEL_BITS = 10

    enum class SEVERITY {
        VERBOSE,
        DEBUG,
        INFO,
        WARN,
        ERROR,
        FATAL,
    }
}
