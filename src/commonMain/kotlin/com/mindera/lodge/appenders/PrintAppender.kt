package com.mindera.lodge.appenders

import com.mindera.lodge.Appender
import com.mindera.lodge.LOG.SEVERITY
import com.mindera.lodge.LOG.SEVERITY.VERBOSE

class PrintAppender(
    id: String,
    level: SEVERITY,
) : Appender {

    constructor(id: String) : this (id = id, level = VERBOSE)
    constructor(level: SEVERITY) : this (id = "PrintAppender", level = level)
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
        with(severity.name) {
            println("$this: $tag $log")
            t?.let {
                println("$this: $tag ${it.stackTraceToString()}")
            }
        }
    }
}
