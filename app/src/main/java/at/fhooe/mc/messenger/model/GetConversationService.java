package at.fhooe.mc.messenger.model;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetConversationService {

    @Headers({
            "Content-type: application/json"
    })

    @GET("/api/conversations?sort=createdDate,asc&size=30")
    Call<List<Conversation>> fetchAllConversations(@Query("page") int page);

    @GET("/api/conversations/count")
    Call<Integer> fetchConversationCount();

    @GET("/api/conversations/{id}")
    Call<Conversation> getConversation(@Path("id") String id);
}

