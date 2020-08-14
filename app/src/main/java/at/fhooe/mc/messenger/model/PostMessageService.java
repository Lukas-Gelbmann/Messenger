package at.fhooe.mc.messenger.model;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface PostMessageService {

    @Headers({
            "Content-type: application/json"
    })
    @POST("/api/messages")
    Call<Message> createMessage(@Body Message message);
}
