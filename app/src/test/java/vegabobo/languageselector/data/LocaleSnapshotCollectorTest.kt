package vegabobo.languageselector.data

import java.util.concurrent.CancellationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Test

class LocaleSnapshotCollectorTest {
    @Test
    fun `successful empty reads produce a valid empty snapshot`() {
        assertEquals(emptyList<AppLocaleEntry>(), collectLocaleSnapshot(listOf("a")) { "" })
    }

    @Test
    fun `modified apps are sorted and successful empty apps omitted`() {
        val tags = mapOf("z" to "ja", "a" to "en-US", "default" to "")
        assertEquals(
            listOf(AppLocaleEntry("a", "en-US"), AppLocaleEntry("z", "ja")),
            collectLocaleSnapshot(tags.keys.toList()) { tags[it] },
        )
    }

    @Test(expected = IllegalStateException::class)
    fun `failed read cannot produce an empty backup`() {
        collectLocaleSnapshot(listOf("failed")) { null }
    }

    @Test(expected = IllegalStateException::class)
    fun `one failed read rejects the entire partial snapshot`() {
        collectLocaleSnapshot(listOf("known", "failed")) { if (it == "known") "en" else null }
    }

    @Test
    fun `cancellation is propagated rather than converted to an empty snapshot`() {
        val cancellation = CancellationException("cancelled")
        try {
            collectLocaleSnapshot(listOf("a")) { throw cancellation }
            fail("Expected cancellation")
        } catch (e: CancellationException) {
            assertSame(cancellation, e)
        }
    }
}
