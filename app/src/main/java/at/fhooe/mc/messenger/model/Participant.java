package at.fhooe.mc.messenger.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Participant {

    public String createdBy;
    public String createdDate;
    public String lastModifiedBy;
    public String lastModifiedDate;
    @NonNull
    @PrimaryKey(autoGenerate = false)
    public String id;
    public String firstName;
    public String lastName;
    public String email;
    public String avatar;


    public Participant(String firstName, String lastName, String email){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}
