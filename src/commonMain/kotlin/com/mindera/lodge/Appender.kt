package com.mindera.lodge

/**
 * Interface for all Log appenders
 */
interface Appender {

    /**
     * Appender minimum log level {@see com.mindera.lodge.LOG.SEVERITY}
     */
    val minLogLevel: LOG.SEVERITY

    /**
     * LOG id (it should be unique within Appender)
     */
    val loggerId: String

    /**
     * Write a log
     *
     * @param severity  Type of log
     * @param tag       Type of log
     * @param t         Throwable
     * @param log       The message you would like logged.
     */
    fun log(severity: LOG.SEVERITY, tag: String, t: Throwable?, log: String)
}
