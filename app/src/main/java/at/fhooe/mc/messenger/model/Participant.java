package at.fhooe.mc.messenger.model;

public class Participant {

    public String createdBy;
    public String createdDate;
    public String lastModifiedBy;
    public String lastModifiedDate;
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
