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
    @GET("/api/conversations?sort=createdDate,asc")
    Call<List<Conversation>> fetchAllConversations();

    @GET("/api/conversations/{id}")
    Call<List<Conversation>> getConversation(@Path("id") String id);
}

