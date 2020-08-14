package at.fhooe.mc.messenger

import android.os.Parcel
import androidx.test.ext.junit.runners.AndroidJUnit4
import at.fhooe.mc.messenger.model.Conversation
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class IntegrationTest {

    /**
     * Test parcelable Conversation
     */
    @Test
    fun testTestClassParcelable() {
        val conversation = Conversation("conversation topic")
        conversation.id = "100"
        conversation.lastModifiedBy = "some_user"
        conversation.createdBy = "another user"
        conversation.createdDate = "000"

        // Obtain a Parcel object and write the parcelable object to it:
        val parcel = Parcel.obtain()
        conversation.writeToParcel(parcel, 0)

        // After you're done with writing, you need to reset the parcel for reading:
        parcel.setDataPosition(0)

        // Reconstruct object from parcel and asserts:
        val createdFromParcel: Conversation = Conversation.CREATOR.createFromParcel(parcel)
        assertEquals(conversation, createdFromParcel)
    }

}
