package com.mindera.lodge

/**
 * LOG static class. It is used to abstract the LOG and have multiple possible implementations
 * It is used also to serve as static references for logging methods to be called.
 */
object LOG {

    /**
     * List of appenders (it can be improved to an ArrayMap if we want to add the support lib as dependency
     */
    private val appenders: MutableSet<Appender> = mutableSetOf()

    /**
     * Enable log appender
     *
     * @param appender Log appender to enable
     */
    fun add(appender: Appender) {
        this.appenders.add(appender)
    }

    /**
     * Enable log appenders
     *
     * @param appenders Log appenders to enable
     */
    fun add(appenders: List<Appender>) {
        this.appenders.addAll(appenders)
    }

    /**
     * Remove log appenders
     *
     * @param id Log id of the loggers to be removed
     */
    fun remove(ids: String) {
        this.appenders.removeAll { ids == it.loggerId }
    }

    /**
     * Remove log appenders
     *
     * @param ids Log ids of each of the loggers enabled by the order sent
     */
    fun remove(ids: Set<String>) {
        this.appenders.removeAll { ids.contains(it.loggerId) }
    }

    /**
     * Log with a VERBOSE level
     *
     * @param tag  Used to identify the source of a log message.
     * @param text The message you would like logged.
     */
    fun v(tag: String, text: String) {
        log(tag, SEVERITY.VERBOSE, null, text)
    }

    /**
     * Log with a VERBOSE level
     *
     * @param tag  Used to identify the source of a log message.
     * @param t    Throwable
     * @param text The message you would like logged.
     */
    fun v(tag: String, t: Throwable, text: String) {
        log(tag, SEVERITY.VERBOSE, t, text)
    }

    /**
     * Log with a DEBUG level
     *
     * @param tag  Used to identify the source of a log message.
     * @param text The message you would like logged.
     */
    fun d(tag: String, text: String) {
        log(tag, SEVERITY.DEBUG, null, text)
    }

    /**
     * Log with a DEBUG level
     *
     * @param tag  Used to identify the source of a log message.
     * @param t    Throwable
     * @param text The message you would like logged.
     */
    fun d(tag: String, t: Throwable, text: String) {
        log(tag, SEVERITY.DEBUG, t, text)
    }

    /**
     * Log with a INFO level
     *
     * @param tag  Used to identify the source of a log message.
     * @param text The message you would like logged.
     */
    fun i(tag: String, text: String) {
        log(tag, SEVERITY.INFO, null, text)
    }

    /**
     * Log with a INFO level
     *
     * @param tag  Used to identify the source of a log message.
     * @param t    Throwable
     * @param text The message you would like logged.
     */
    fun i(tag: String, t: Throwable, text: String) {
        log(tag, SEVERITY.INFO, t, text)
    }

    /**
     * Log with a WARN level
     *
     * @param tag  Used to identify the source of a log message.
     * @param text The message you would like logged.
     */
    fun w(tag: String, text: String) {
        log(tag, SEVERITY.WARN, null, text)
    }

    /**
     * Log with a WARN level
     *
     * @param tag  Used to identify the source of a log message.
     * @param t    Throwable
     * @param text The message you would like logged.
     */
    fun w(tag: String, t: Throwable, text: String) {
        log(tag, SEVERITY.WARN, t, text)
    }

    /**
     * Log with a ERROR level
     *
     * @param tag  Used to identify the source of a log message.
     * @param text The message you would like logged.
     */
    fun e(tag: String, text: String) {
        log(tag, SEVERITY.ERROR, null, text)
    }

    /**
     * Log with a ERROR level
     *
     * @param tag  Used to identify the source of a log message.
     * @param t    Throwable
     * @param text The message you would like logged.
     */
    fun e(tag: String, t: Throwable, text: String) {
        log(tag, SEVERITY.ERROR, t, text)
    }

    /**
     * Log a What a Terrible Failure: Report an exception that should never happen.
     *
     * @param tag  Used to identify the source of a log message.
     * @param text The message you would like logged.
     */
    fun wtf(tag: String, text: String) {
        log(tag, SEVERITY.FATAL, null, text)
    }

    /**
     * Log a What a Terrible Failure: Report an exception that should never happen.
     *
     * @param tag  Used to identify the source of a log message.
     * @param t    Throwable
     * @param text The message you would like logged.
     */
    fun wtf(tag: String, t: Throwable, text: String) {
        log(tag, SEVERITY.FATAL, t, text)
    }

    private fun log(
        tag: String,
        severity: SEVERITY,
        t: Throwable?,
        text: String
    ) {
        if(appenders.isNotEmpty()) {
            val log = "[T#$threadName] | $text"
            appenders.forEach {
                if (it.minLogLevel.ordinal > severity.ordinal) return@forEach
                it.log(severity, tag, t, log)
            }
        }
    }

    enum class SEVERITY {
        VERBOSE, DEBUG, INFO, WARN, ERROR, FATAL
    }
}
