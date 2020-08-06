package at.fhooe.mc.messenger.model;

import retrofit2.Call;
import retrofit2.http.*;

@SuppressWarnings("HardCodedStringLiteral")
public interface PostParticipantService {

    @Headers({

            "Content-type: application/json" //NON-NLS

    })
    @POST("/api/participants") //NON-NLS
    Call<Participant> sendParticipant(@Body Participant participant);
}
