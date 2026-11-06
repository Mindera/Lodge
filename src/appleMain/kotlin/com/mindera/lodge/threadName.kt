package com.mindera.lodge

import platform.Foundation.NSThread

internal actual val threadName: String
    get() = with(NSThread.currentThread) {
        if (isMainThread) "main" else name.orEmpty().ifEmpty { hash.toString() }
    }
