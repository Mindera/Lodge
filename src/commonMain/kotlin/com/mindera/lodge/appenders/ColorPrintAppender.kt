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

class ColorPrintAppender(
    id: String,
    level: SEVERITY,
) : Appender {

    constructor(id: String) : this (id = id, level = VERBOSE)
    constructor(level: SEVERITY) : this (id = "ColorPrintAppender", level = level)
    constructor() : this (id = "PrintAppender")

    /**
     * Appender ID
     */
    override val loggerId: String = id

    /**
     * Minimum log severity for this appender.
     */
    override val minLogLevel: SEVERITY = level

    override fun log(severity: SEVERITY, tag: String, t: Throwable?, log: String) {
        val prefix = severity.prefix(tag)
        println("$prefix$log$ANSI_RESET")
        t?.let { println("$prefix${it.stackTraceToString()}$ANSI_RESET") }
    }

    private fun SEVERITY.prefix(tag: String) = "$emoji$color | $initial | $tag: "

    private val SEVERITY.color: String get() = when (this) {
        VERBOSE -> ANSI_FAINT
        DEBUG -> ANSI_CYAN
        INFO -> ANSI_GREEN
        WARN -> ANSI_YELLOW
        ERROR -> ANSI_RED
        FATAL -> ANSI_PURPLE
    }
}

private const val ANSI_RESET = "\u001B[0m"
private const val ANSI_CYAN = "\u001B[36m"
private const val ANSI_GREEN = "\u001B[32m"
private const val ANSI_PURPLE = "\u001B[35m"
private const val ANSI_YELLOW = "\u001B[33m"
private const val ANSI_RED = "\u001B[31m"
private const val ANSI_BOLD = "\u001B[1m"
private const val ANSI_FAINT = "\u001B[2m"
