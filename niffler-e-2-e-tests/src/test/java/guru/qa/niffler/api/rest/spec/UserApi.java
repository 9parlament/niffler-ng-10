package guru.qa.niffler.api.rest.spec;

import guru.qa.niffler.model.api.UserJson;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserApi {

    @POST("/internal/invitations/send")
    Call<UserJson> sendInvitation(@Query("username") String username,
                                  @Query("targetUsername") String targetUsername);

    @POST("/internal/invitations/accept")
    Call<UserJson> acceptInvitation(@Query("username") String username,
                                    @Query("targetUsername") String targetUsername);
}
