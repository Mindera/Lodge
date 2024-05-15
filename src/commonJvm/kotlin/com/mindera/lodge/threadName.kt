package com.mindera.lodge

internal actual val threadName: String
    get() = with(Thread.currentThread()) {
        name.ifEmpty { id.toString() }
    }
