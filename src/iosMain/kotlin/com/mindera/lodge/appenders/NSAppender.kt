package com.mindera.lodge.appenders

import com.mindera.lodge.LOG.SEVERITY
import com.mindera.lodge.LOG.SEVERITY.VERBOSE
import com.mindera.lodge.Appender
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
        with(severity.name) {
            NSLog("%s: %s %@", this, tag, log as NSString)
            t?.let {
                NSLog("%s: %s %@", this, tag, it.stackTraceToString() as NSString)
            }
        }
    }
}
