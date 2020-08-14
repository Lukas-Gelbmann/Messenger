package at.fhooe.mc.messenger.model;

import retrofit2.Call;
import retrofit2.http.*;

public interface PostParticipantService {

    @Headers({
            "Content-type: application/json"
    })
    @POST("/api/participants")
    Call<Participant> sendParticipant(@Body Participant participant);
}
