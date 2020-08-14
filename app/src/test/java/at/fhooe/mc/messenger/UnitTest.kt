package at.fhooe.mc.messenger

import at.fhooe.mc.messenger.view.MainActivity
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit Tests.
 * Project does not have any Java/Kotlin logic which can be used without Android Context.
 */
class UnitTest {

    @Test
    fun database_name_isCorrect() {
        assertEquals("Messenger", MainActivity.DATABASE_NAME)
    }

    @Test
    fun job_notification_id_isCorrect() {
        assertEquals(1, MainActivity.JOB_NOTIFICATION_ID)
    }
}
