package com.mindera.lodge.appenders

import android.util.Log
import com.mindera.lodge.Appender
import com.mindera.lodge.LOG.SEVERITY
import com.mindera.lodge.LOG.SEVERITY.DEBUG
import com.mindera.lodge.LOG.SEVERITY.ERROR
import com.mindera.lodge.LOG.SEVERITY.FATAL
import com.mindera.lodge.LOG.SEVERITY.INFO
import com.mindera.lodge.LOG.SEVERITY.VERBOSE
import com.mindera.lodge.LOG.SEVERITY.WARN

/**
 * Log appender for Logcat
 */
class LogcatAppender(
    id: String = "LogcatAppender",
    level: SEVERITY = VERBOSE,
    private val maxLineLength: Int = 4000,
    private val isSplitLinesAboveMaxLength: Boolean = true,
) : Appender {

    /**
     * Appender ID
     */
    override val loggerId: String = id

    /**
     * Minimum log severity for this appender.
     */
    override val minLogLevel: SEVERITY = level

    override fun log(severity: SEVERITY, tag: String, t: Throwable?, log: String) {
        val text = t?.let { "$log\n${it.stackTraceToString()}" } ?: log
        for (chunk in text.toChunks()) {
            when (severity) {
                VERBOSE -> Log.v(tag, chunk)
                DEBUG -> Log.d(tag, chunk)
                INFO -> Log.i(tag, chunk)
                WARN -> Log.w(tag, chunk)
                ERROR -> Log.e(tag, chunk)
                FATAL -> Log.wtf(tag, chunk)
            }
        }
    }

    /**
     * Formats the log to respect the value defined in MAX_LINE_LENGTH
     *
     * @return A list of lines to log
     */
    private fun String.toChunks(): List<String> {
        return if (length <= maxLineLength || !isSplitLinesAboveMaxLength) {
            listOf(this)
        } else {
            chunked(maxLineLength)
        }
    }
}
