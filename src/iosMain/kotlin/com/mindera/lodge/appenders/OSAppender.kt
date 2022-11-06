package com.mindera.lodge.appenders

import com.mindera.lodge.LOG.SEVERITY
import com.mindera.lodge.LOG.SEVERITY.VERBOSE
import com.mindera.lodge.LOG.SEVERITY.DEBUG
import com.mindera.lodge.LOG.SEVERITY.INFO
import com.mindera.lodge.LOG.SEVERITY.WARN
import com.mindera.lodge.LOG.SEVERITY.ERROR
import com.mindera.lodge.LOG.SEVERITY.FATAL
import com.mindera.lodge.Appender
import kotlinx.cinterop.ptr
import platform.darwin.OS_LOG_DEFAULT
import platform.darwin.OS_LOG_TYPE_DEBUG
import platform.darwin.OS_LOG_TYPE_DEFAULT
import platform.darwin.OS_LOG_TYPE_ERROR
import platform.darwin.OS_LOG_TYPE_INFO
import platform.darwin.__dso_handle
import platform.darwin._os_log_internal
import platform.darwin.os_log_type_enabled
import platform.darwin.os_log_type_t

class OSAppender(
    id: String,
    level: SEVERITY,
) : Appender {

    constructor() : this (id = "OSAppender", level = VERBOSE)

    /**
     * Appender ID
     */
    override val loggerId: String = id

    /**
     * Minimum log severity for this appender.
     */
    override val minLogLevel: SEVERITY = level

    override fun log(severity: SEVERITY, tag: String, t: Throwable?, log: String) {
        val prefix = "${severity.name}: $tag"
        with(severity.toLevel()) {
            log(prefix + log)
            t?.let {
                log(prefix + it.stackTraceToString())
            }
        }
    }

    private fun os_log_type_t.log(message: String) {
        with(OS_LOG_DEFAULT) {
            if (os_log_type_enabled(this, this@log)) {
                _os_log_internal(__dso_handle.ptr, this, this@log, message)
            }
        }
    }

    private fun SEVERITY.toLevel(): os_log_type_t {
        return when (this) {
            VERBOSE -> OS_LOG_TYPE_DEBUG
            DEBUG -> OS_LOG_TYPE_DEBUG
            INFO -> OS_LOG_TYPE_INFO
            WARN -> OS_LOG_TYPE_DEFAULT
            ERROR -> OS_LOG_TYPE_ERROR
            FATAL -> OS_LOG_TYPE_ERROR
        }
    }
}
