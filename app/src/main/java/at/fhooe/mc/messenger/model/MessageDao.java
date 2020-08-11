package at.fhooe.mc.messenger.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;
@Dao
public interface MessageDao {

    @Query("SELECT * FROM message")
    List<Message> getMessages();

    @Query("SELECT * FROM message WHERE id = :id")
    Message getMessage(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Message... messages);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Message message);

    @Delete
    void delete(Message message);

}
