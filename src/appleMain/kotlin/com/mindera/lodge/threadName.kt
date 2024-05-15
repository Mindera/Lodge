package com.mindera.lodge

import platform.Foundation.NSThread

internal actual val threadName: String
    get() = with(NSThread.currentThread) {
        name.toString().ifEmpty { hash.toString() }
    }
