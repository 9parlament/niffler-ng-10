package guru.qa.niffler.service;

import guru.qa.niffler.api.UserApi;
import guru.qa.niffler.model.UserJson;
import lombok.SneakyThrows;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static guru.qa.niffler.config.Configuration.CFG;
import static org.apache.hc.core5.http.HttpStatus.SC_OK;

public class UserApiClient implements ApiClient {
    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CFG.apiUserdataUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final UserApi userApi = retrofit.create(UserApi.class);

    @SneakyThrows
    public UserJson sendInvitation(String username, String targetUsername) {
        return executeWithAssert(
                userApi.sendInvitation(username, targetUsername),
                SC_OK,
                "При отправке приглашения дружбы пользователю %s от пользователя %s возникла ошибка".formatted(targetUsername, username)
        );
    }

    @SneakyThrows
    public UserJson acceptInvitation(String username, String targetUsername) {
        return executeWithAssert(
                userApi.acceptInvitation(username, targetUsername),
                SC_OK,
                "При отправке подтверждения дружбы пользователю %s от пользователя %s возникла ошибка".formatted(targetUsername, username)
        );
    }
}
