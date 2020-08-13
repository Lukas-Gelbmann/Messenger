package at.fhooe.mc.messenger.model;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface GetParticipantService {
    @Headers({

            "Content-type: application/json"

    })

    @GET("/api/participants?sort=createdDate,asc&page=0&size=1000") //TODO pagination logic
    Call<List<Participant>> fetchAllParticipants();


    @GET("/api/participants/{id}")
    Call<Participant> getParticipant(@Path("id") String id);
}
