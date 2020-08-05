package at.fhooe.mc.messenger.model;

public class Conversation {
    public String createdBy;
    public String createdDate;
    public String lastModifiedBy;
    public String lastModifiedDate;
    public String id;
    public String topic;

    public Conversation(String topic) {
        this.topic = topic;
    }
}
