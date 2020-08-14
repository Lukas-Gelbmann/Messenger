package at.fhooe.mc.messenger.model;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetParticipantService {

    @Headers({
            "Content-type: application/json"
    })

    @GET("/api/participants?sort=createdDate,asc&size=30")
    Call<List<Participant>> fetchAllParticipants(@Query("page") int page);

    @GET("/api/participants/count")
    Call<Integer> fetchParticipantCount();

    @GET("/api/participants/{id}")
    Call<Participant> getParticipant(@Path("id") String id);
}
