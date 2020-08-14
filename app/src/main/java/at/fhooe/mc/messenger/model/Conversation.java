package at.fhooe.mc.messenger.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class Conversation implements Parcelable {

    public String createdBy;
    public String createdDate;
    public String lastModifiedBy;
    public String lastModifiedDate;
    @NonNull
    @PrimaryKey
    public String id;
    public String topic;

    public Conversation(String topic) {
        this.topic = topic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conversation that = (Conversation) o;
        return Objects.equals(createdBy, that.createdBy) &&
                Objects.equals(createdDate, that.createdDate) &&
                Objects.equals(lastModifiedBy, that.lastModifiedBy) &&
                Objects.equals(lastModifiedDate, that.lastModifiedDate) &&
                id.equals(that.id) &&
                Objects.equals(topic, that.topic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(createdBy, createdDate, lastModifiedBy, lastModifiedDate, id, topic);
    }

    public Conversation(Parcel in) {
        String[] data = new String[6];
        in.readStringArray(data);
        this.createdBy = data[0];
        this.createdDate = data[1];
        this.lastModifiedBy = data[2];
        this.lastModifiedDate = data[3];
        this.id = data[4];
        this.topic = data[5];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{createdBy, createdDate, lastModifiedBy, lastModifiedDate, id, topic});
    }

    public static final Parcelable.Creator<Conversation> CREATOR = new Parcelable.Creator<Conversation>() {

        @Override
        public Conversation createFromParcel(Parcel source) {
            return new Conversation(source);
        }

        @Override
        public Conversation[] newArray(int size) {
            return new Conversation[size];
        }
    };

}