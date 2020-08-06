package at.fhooe.mc.messenger.model;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface GetMessageService {

    @Headers({
            "Content-type: application/json"
    })

    @GET("/api/messages")
    Call<List<Message>> fetchAllMessages();

    @GET("/api/messages/{id}")
    Call<List<Message>> getMessage(@Path("id") String id);
}

