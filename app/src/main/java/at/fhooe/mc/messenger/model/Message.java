package at.fhooe.mc.messenger.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Message {

    public String content;
    @NonNull
    @PrimaryKey
    public String id;
    public String conversationId;
    public String senderId;
    public String receiverId;
    public String createdBy;
    public String createdDate;
    public String lastModifiedBy;
    public String lastModifiedDate;

    public Message(String content, String conversationId, String senderId, String receiverId) {
        this.content = content;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

}