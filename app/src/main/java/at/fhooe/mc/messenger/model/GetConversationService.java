package at.fhooe.mc.messenger.model;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface GetConversationService {

    @Headers({

            "Content-type: application/json"

    })
    @GET("/api/conversations?sort=createdDate,asc&page=0&size=1000") //TODO pagination logic
    Call<List<Conversation>> fetchAllConversations();

    @GET("/api/conversations/{id}")
    Call<Conversation> getConversation(@Path("id") String id);
}

