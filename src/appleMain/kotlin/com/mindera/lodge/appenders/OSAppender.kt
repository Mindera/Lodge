package com.mindera.lodge.appenders

import com.mindera.lodge.Appender
import com.mindera.lodge.LOG.SEVERITY
import com.mindera.lodge.LOG.SEVERITY.DEBUG
import com.mindera.lodge.LOG.SEVERITY.ERROR
import com.mindera.lodge.LOG.SEVERITY.FATAL
import com.mindera.lodge.LOG.SEVERITY.INFO
import com.mindera.lodge.LOG.SEVERITY.VERBOSE
import com.mindera.lodge.LOG.SEVERITY.WARN
import com.mindera.lodge.extensions.emoji
import com.mindera.lodge.extensions.initial
import com.mindera.lodge.interop.lodge_os_log
import kotlinx.cinterop.ExperimentalForeignApi
import platform.darwin.OS_LOG_TYPE_DEBUG
import platform.darwin.OS_LOG_TYPE_DEFAULT
import platform.darwin.OS_LOG_TYPE_ERROR
import platform.darwin.OS_LOG_TYPE_FAULT
import platform.darwin.OS_LOG_TYPE_INFO
import platform.darwin.os_log_type_t

class OSAppender(
    id: String,
    level: SEVERITY,
) : Appender {

    constructor(level: SEVERITY) : this ("OSAppender", level)
    constructor() : this (VERBOSE)

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
            val text = t?.let { "$log\n${it.stackTraceToString()}" } ?: log
            text.chunked(MAX_CHUNK_LENGTH).forEach { log(prefix + it) }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun os_log_type_t.log(message: String) = lodge_os_log(this, message)

    private fun SEVERITY.toLevel(): os_log_type_t = when (this) {
        VERBOSE -> OS_LOG_TYPE_DEBUG
        DEBUG -> OS_LOG_TYPE_DEBUG
        INFO -> OS_LOG_TYPE_INFO
        WARN -> OS_LOG_TYPE_DEFAULT
        ERROR -> OS_LOG_TYPE_ERROR
        FATAL -> OS_LOG_TYPE_FAULT
    }
    private fun SEVERITY.prefix(tag: String) = "$emoji | $initial | $tag: "
}

/**
 * The unified log truncates dynamic strings at around 1024 bytes
 */
private const val MAX_CHUNK_LENGTH = 900
