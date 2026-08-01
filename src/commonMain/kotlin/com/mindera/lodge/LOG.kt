package com.mindera.lodge

import com.mindera.lodge.LOG.SEVERITY.DEBUG
import com.mindera.lodge.LOG.SEVERITY.ERROR
import com.mindera.lodge.LOG.SEVERITY.FATAL
import com.mindera.lodge.LOG.SEVERITY.INFO
import com.mindera.lodge.LOG.SEVERITY.VERBOSE
import com.mindera.lodge.LOG.SEVERITY.WARN
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart.UNDISPATCHED
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * LOG static class. It is used to abstract the LOG and have multiple possible implementations
 * It is used also to serve as static references for logging methods to be called.
 */
object LOG {

    /**
     * List of appenders (it can be improved to an ArrayMap if we want to add the support lib as dependency
     */
    private val appenders: MutableSet<Appender> = mutableSetOf()

    /**
     * Work queue
     */
    private val tasks = Channel<() -> Unit>(UNLIMITED)

    private var delayMillis = 0L

    /**
     * A dedicated coroutine that pulls lambdas out of `tasks` and executes
     * them one-by-one, preserving the exact order in which they were queued.
     */
    @Suppress("unused")
    private val scope = CoroutineScope(SupervisorJob() + Default.limitedParallelism(1)).apply {
        launch(start = UNDISPATCHED) {
            for (task in tasks) {
                runCatching { task() }
                delay(delayMillis)
            }
        }
    }

    /**
     * Enable log appender. No-op if an appender with the same [Appender.loggerId] is already registered.
     *
     * @param appender Log appender to enable
     */
    fun add(appender: Appender) {
        tasks.trySend {
            if (appenders.none { it.loggerId == appender.loggerId }) {
                appenders.add(appender)
            } else {
                log("LOG", WARN, null) { "Appender '${appender.loggerId}' discarded: an appender with that id is already registered." }
            }
        }
    }

    /**
     * Enable log appenders. Skips any appender whose [Appender.loggerId] is already registered.
     *
     * @param appenders Log appenders to enable
     */
    fun add(appenders: List<Appender>) {
        tasks.trySend {
            appenders.forEach { candidate ->
                if (this.appenders.none { it.loggerId == candidate.loggerId }) {
                    this.appenders.add(candidate)
                } else {
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
        tasks.trySend { appenders.removeAll { id == it.loggerId } }
    }

    /**
     * Remove log appenders
     *
     * @param ids Log ids of each of the loggers enabled by the order sent
     */
    fun remove(ids: Set<String>) {
        tasks.trySend { appenders.removeAll { ids.contains(it.loggerId) } }
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
        val originalThread = threadName
        tasks.trySend {
            if (appenders.isNotEmpty()) {
                val log = "[T#$originalThread] | ${message()}"
                appenders.forEach {
                    if (it.minLogLevel.ordinal > severity.ordinal) return@forEach
                    it.log(severity, tag, t, log)
                }
            }
        }
    }

    enum class SEVERITY {
        VERBOSE,
        DEBUG,
        INFO,
        WARN,
        ERROR,
        FATAL,
    }
}
