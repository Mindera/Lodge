package com.mindera.lodge.appenders

import com.mindera.lodge.Appender
import com.mindera.lodge.LOG.SEVERITY
import com.mindera.lodge.LOG.SEVERITY.VERBOSE
import platform.Foundation.NSLog
import platform.Foundation.NSString

class NSAppender(
    id: String,
    level: SEVERITY,
) : Appender {

    constructor() : this (
        id = "NSAppender",
        level = VERBOSE
    )

    /**
     * Appender ID
     */
    override val loggerId: String = id

    /**
     * Minimum log severity for this appender.
     */
    override val minLogLevel: SEVERITY = level

    @Suppress("CAST_NEVER_SUCCEEDS")
    override fun log(severity: SEVERITY, tag: String, t: Throwable?, log: String) {
        val level = severity.name as NSString
        val tagText = tag as NSString
        NSLog("%@: %@ %@", level, tagText, log as NSString)
        t?.let {
            NSLog("%@: %@ %@", level, tagText, it.stackTraceToString() as NSString)
        }
    }
}
