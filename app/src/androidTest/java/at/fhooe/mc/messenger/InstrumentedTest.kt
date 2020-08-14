package at.fhooe.mc.messenger

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import at.fhooe.mc.messenger.model.Conversation
import at.fhooe.mc.messenger.view.MessagingActivity
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class InstrumentedTest {


    /**
     * Launch messaging Activity with a conversation in its intent.
     */
    @get:Rule
    val mActivityTestRule: ActivityTestRule<MessagingActivity> =
        object : ActivityTestRule<MessagingActivity>(MessagingActivity::class.java) {
            override fun getActivityIntent(): Intent {
                val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
                val conversation = Conversation("")
                conversation.id = "2"
                return Intent(targetContext, MessagingActivity::class.java).apply {
                    putExtra(
                        "CONVERSATION_ID",
                        conversation
                    )
                }
            }
        }


    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("at.fhooe.mc.messenger", appContext.packageName)
    }

    /**
     * Test entering sending message and clicking the send Button.
     */
    @Test
    fun sendMessage() {
        val message = "This is a text message."
        onView(withId(R.id.edit_text_message)).perform(typeText(message))
        onView(withId(R.id.button_send_message)).perform(click())
        onView(withId(R.id.edit_text_message)).check(matches(withText("")))
    }
}
