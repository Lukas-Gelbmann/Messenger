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

    @GET("/api/messages")
    Call<List<Message>> getAllMessages(
            @Query("conversationId") String id
    );

    //e.g. api/messages?conversationId.equals=2
    @GET("/api/messages?sort=createdDate,asc")
    Call<List<Message>> getMessagesForConversation(@Query("conversationId.equals") String conversationId);

    @GET("/api/messages/{id}")
    Call<List<Message>> getMessage(@Path("id") String id);

    @GET("/api/messages")
    Call<List<Message>> getMessageForParticipant(@Query("receiverId.equals") String receiverId);
}

