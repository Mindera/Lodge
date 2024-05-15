package com.mindera.lodge.appenders

import com.mindera.lodge.Appender
import com.mindera.lodge.LOG.SEVERITY
import com.mindera.lodge.LOG.SEVERITY.DEBUG
import com.mindera.lodge.LOG.SEVERITY.ERROR
import com.mindera.lodge.LOG.SEVERITY.FATAL
import com.mindera.lodge.LOG.SEVERITY.INFO
import com.mindera.lodge.LOG.SEVERITY.VERBOSE
import com.mindera.lodge.LOG.SEVERITY.WARN
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ptr
import platform.darwin.OS_LOG_DEFAULT
import platform.darwin.OS_LOG_TYPE_DEBUG
import platform.darwin.OS_LOG_TYPE_DEFAULT
import platform.darwin.OS_LOG_TYPE_ERROR
import platform.darwin.OS_LOG_TYPE_INFO
import platform.darwin.__dso_handle
import platform.darwin._os_log_internal
import platform.darwin.os_log_type_t

class OSAppender(
    id: String,
    level: SEVERITY,
    private val formatString: String,
) : Appender {

    constructor() : this ("OSAppender", VERBOSE, "%{public}s")

    /**
     * Appender ID
     */
    override val loggerId: String = id

    /**
     * Minimum log severity for this appender.
     */
    override val minLogLevel: SEVERITY = level

    override fun log(severity: SEVERITY, tag: String, t: Throwable?, log: String) {
        with(severity.toLevel()) {
            val prefix = severity.prefix(tag)
            log(prefix + log)
            t?.let {
                log(prefix + it.stackTraceToString())
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun os_log_type_t.log(
        message: String,
    ) = _os_log_internal(__dso_handle.ptr, OS_LOG_DEFAULT, this, formatString, message)

    private fun SEVERITY.toLevel(): os_log_type_t = when (this) {
        VERBOSE -> OS_LOG_TYPE_DEBUG
        DEBUG -> OS_LOG_TYPE_DEBUG
        INFO -> OS_LOG_TYPE_INFO
        WARN -> OS_LOG_TYPE_DEFAULT
        ERROR -> OS_LOG_TYPE_ERROR
        FATAL -> OS_LOG_TYPE_ERROR
    }

    private fun SEVERITY.prefix(tag: String) = "$emoji $name: $tag"

    // Kudos to Napier https://github.com/AAkira/Napier#darwinios-macos-watchos-tvosintelapple-silicon
    private val SEVERITY.emoji: String get()= when (this) {
        VERBOSE -> "⚪"
        DEBUG -> "🔵"
        INFO -> "🟢"
        WARN -> "🟡"
        ERROR -> "🔴"
        FATAL -> "🟤"
    }

}
