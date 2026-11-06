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
        val chunks = log.toChunks()
        chunks.forEachIndexed { index, chunk ->
            val throwable = t.takeIf { index == chunks.lastIndex }
            when (severity) {
                VERBOSE -> Log.v(tag, chunk, throwable)
                DEBUG -> Log.d(tag, chunk, throwable)
                INFO -> Log.i(tag, chunk, throwable)
                WARN -> Log.w(tag, chunk, throwable)
                ERROR -> Log.e(tag, chunk, throwable)
                FATAL -> Log.wtf(tag, chunk, throwable)
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
