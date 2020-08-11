package at.fhooe.mc.messenger.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;
@Dao
public interface ParticipantDao {

    @Query("SELECT * FROM participant")
    List<Participant> getParticipants();

    @Query("SELECT * FROM participant WHERE id = :id")
    Participant getParticipant(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Participant... participants);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Participant participant);

    @Delete
    void delete(Participant participant);

}
