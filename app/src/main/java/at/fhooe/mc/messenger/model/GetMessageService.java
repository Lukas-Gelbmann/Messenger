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


    @GET("/api/messages?sort=createdDate,asc&size=30") //e.g. api/messages?conversationId.equals=2
    Call<List<Message>> getMessagesForConversation(@Query("conversationId.equals") String conversationId ,@Query("page") int page);

    @GET("/api/messages?sort=createdDate,asc&size=30")
    Call<List<Message>> getMessagesForParticipant(@Query("receiverId.equals") String receiverId,@Query("page") int page);

    @GET("/api/messages?sort=createdDate,desc")
    Call<List<Message>> getNewestMessagesForParticipant(@Query("receiverId.equals") String receiverId, @Query("size") int i);

    @GET("/api/messages/count")
    Call<Integer> getMessageCountForParticipant(@Query("receiverId.equals") String receiverId);

    @GET("/api/messages/{id}")
    Call<List<Message>> getMessage(@Path("id") String id);

}

