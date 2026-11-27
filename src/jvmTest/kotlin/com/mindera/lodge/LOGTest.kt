package com.mindera.lodge

import com.mindera.lodge.LOG.SEVERITY
import com.mindera.lodge.LOG.SEVERITY.ERROR
import com.mindera.lodge.LOG.SEVERITY.VERBOSE
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit.SECONDS
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LOGTest {

    private class TestAppender(
        override val loggerId: String,
        override val minLogLevel: SEVERITY,
        private val onLog: () -> Unit,
    ) : Appender {
        override fun log(severity: SEVERITY, tag: String, t: Throwable?, log: String) = onLog()
    }

    @Test
    fun failingAppenderDoesNotBlockTheNextOne() {
        val received = CountDownLatch(1)
        LOG.add(TestAppender("failing", VERBOSE) { error("boom") })
        LOG.add(TestAppender("next", VERBOSE) { received.countDown() })

        LOG.i("tag", "message")

        assertTrue(received.await(5, SECONDS))
        LOG.remove(setOf("failing", "next"))
    }

    @Test
    fun lazyMessageIsNotBuiltWhenNoAppenderAcceptsIt() {
        val received = CountDownLatch(1)
        var built = false
        LOG.add(TestAppender("errors", ERROR) { received.countDown() })

        LOG.v("tag") {
            built = true
            "message"
        }
        LOG.e("tag", "done")

        assertTrue(received.await(5, SECONDS))
        assertFalse(built)
        LOG.remove("errors")
    }

    @Test
    fun flushWaitsForQueuedLogs() = runBlocking {
        var logged = false
        LOG.add(TestAppender("flush", VERBOSE) { logged = true })

        LOG.i("tag", "message")
        LOG.flush()

        assertTrue(logged)
        LOG.remove("flush")
    }

    @Test
    fun logRightAfterAddIsDelivered() {
        val received = CountDownLatch(1)
        LOG.add(TestAppender("verbose", VERBOSE) { received.countDown() })

        LOG.v("tag", "message")

        assertTrue(received.await(5, SECONDS))
        LOG.remove("verbose")
    }

    @Test
    fun removedAppenderNoLongerKeepsItsLevelOpen() {
        val received = CountDownLatch(1)
        var built = false
        LOG.add(TestAppender("verbose", VERBOSE) {})
        LOG.add(TestAppender("errors", ERROR) { received.countDown() })
        LOG.remove("verbose")

        LOG.v("tag") {
            built = true
            "message"
        }
        LOG.e("tag", "done")

        assertTrue(received.await(5, SECONDS))
        assertFalse(built)
        LOG.remove("errors")
    }

    @Test
    fun discardedDuplicateDoesNotKeepItsLevelOpen() {
        val received = CountDownLatch(1)
        var built = false
        LOG.add(TestAppender("errors", ERROR) { received.countDown() })
        LOG.add(TestAppender("errors", VERBOSE) {})
        LOG.e("tag", "flush")
        assertTrue(received.await(5, SECONDS))

        val done = CountDownLatch(1)
        LOG.add(TestAppender("done", ERROR) { done.countDown() })
        LOG.v("tag") {
            built = true
            "message"
        }
        LOG.e("tag", "done")

        assertTrue(done.await(5, SECONDS))
        assertFalse(built)
        LOG.remove(setOf("errors", "done"))
    }
}
