package com.mindera.lodge.extensions

import com.mindera.lodge.LOG.SEVERITY
import com.mindera.lodge.LOG.SEVERITY.DEBUG
import com.mindera.lodge.LOG.SEVERITY.ERROR
import com.mindera.lodge.LOG.SEVERITY.FATAL
import com.mindera.lodge.LOG.SEVERITY.INFO
import com.mindera.lodge.LOG.SEVERITY.VERBOSE
import com.mindera.lodge.LOG.SEVERITY.WARN

// Kudos to Napier https://github.com/AAkira/Napier#darwinios-macos-watchos-tvosintelapple-silicon
internal val SEVERITY.emoji: String get() = when (this) {
    VERBOSE -> "⚪"
    DEBUG -> "🔵"
    INFO -> "🟢"
    WARN -> "🟡"
    ERROR -> "🔴"
    FATAL -> "🟣"
}

internal val SEVERITY.initial: String get() = when (this) {
    VERBOSE -> "V"
    DEBUG -> "D"
    INFO -> "I"
    WARN -> "W"
    ERROR -> "E"
    FATAL -> "F"
}
