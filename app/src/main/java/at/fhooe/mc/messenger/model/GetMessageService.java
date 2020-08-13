package at.fhooe.mc.messenger.model;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetMessageService {

    @Headers({
            "Content-type: application/json"
    })

    //e.g. api/messages?conversationId.equals=2
    @GET("/api/messages?sort=createdDate,asc&page=0&size=1000") //TODO pagination logic
    Call<List<Message>> getMessagesForConversation(@Query("conversationId.equals") String conversationId);

    @GET("/api/messages/{id}")
    Call<List<Message>> getMessage(@Path("id") String id);

    @GET("/api/messages?sort=createdDate,asc&page=0&size=1000") //TODO pagination logic
    Call<List<Message>> getMessagesForParticipant(@Query("receiverId.equals") String receiverId);

    @GET("/api/messages?sort=createdDate,desc")
    Call<List<Message>> getNewestMessagesForParticipant(@Query("receiverId.equals") String receiverId);

    @GET("/api/messages/count")
    Call<Integer> getMessageCountForParticipant(@Query("receiverId.equals") String receiverId);
}

