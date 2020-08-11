package at.fhooe.mc.messenger.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ConversationDao {

    @Query("SELECT * FROM conversation")
    List<Conversation> getConversations();

    @Query("SELECT * FROM conversation WHERE id = :id")
    Conversation getConversation(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Conversation... conversations);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Conversation conversation);

    @Delete
    void delete(Conversation conversation);

}
