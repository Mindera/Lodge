package com.mindera.lodge.appenders

import android.util.Log
import com.mindera.lodge.Appender
import com.mindera.lodge.LOG.SEVERITY
import com.mindera.lodge.LOG.SEVERITY.VERBOSE
import com.mindera.lodge.LOG.SEVERITY.DEBUG
import com.mindera.lodge.LOG.SEVERITY.INFO
import com.mindera.lodge.LOG.SEVERITY.WARN
import com.mindera.lodge.LOG.SEVERITY.ERROR
import com.mindera.lodge.LOG.SEVERITY.FATAL
import kotlin.math.ceil

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
        for (chunk in log.toChunks()) {
            when (severity) {
                VERBOSE -> Log.v(tag, chunk, t)
                DEBUG -> Log.d(tag, chunk, t)
                INFO -> Log.i(tag, chunk, t)
                WARN -> Log.w(tag, chunk, t)
                ERROR -> Log.e(tag, chunk, t)
                FATAL -> Log.wtf(tag, chunk, t)
            }
        }
    }

    /**
     * Formats the log to respect the value defined in MAX_LINE_LENGTH
     *
     * @return A list of lines to log
     */
    private fun String.toChunks(): List<String> {
        return mutableListOf<String>().also {
            if (length <= maxLineLength || !isSplitLinesAboveMaxLength) {
                it.add(this@toChunks)
            } else {
                val chunks = ceil((1f * length / maxLineLength).toDouble()).toInt()
                for (i in 1..chunks) {
                    val max = maxLineLength * i
                    if (max < length) {
                        it.add("[Chunk $i of $chunks] " + substring(maxLineLength * (i - 1), max))
                    } else {
                        it.add("[Chunk $i of $chunks] " + substring(maxLineLength * (i - 1)))
                    }
                }
            }
        }
    }
}
