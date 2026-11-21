package vegabobo.languageselector.ui.screen.main

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ModifiedStateTest {
    @Test
    fun `failed reads retain the last known modified state`() {
        assertTrue(resolveModifiedState(true, null, true))
        assertFalse(resolveModifiedState(false, null, true))
    }

    @Test
    fun `successful empty reads remove modified labels`() {
        assertFalse(resolveModifiedState(true, false, true))
    }

    @Test
    fun `successful nonempty reads add modified labels`() {
        assertTrue(resolveModifiedState(false, true, true))
    }

    @Test
    fun `access loss clears even cached or newly observed modifications`() {
        assertFalse(resolveModifiedState(true, null, false))
        assertFalse(resolveModifiedState(true, true, false))
    }
}
