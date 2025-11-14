package guru.qa.niffler.api.rest;

import guru.qa.niffler.api.rest.spec.AuthApi;
import guru.qa.niffler.api.rest.spec.UserApi;
import guru.qa.niffler.model.api.UserJson;
import guru.qa.niffler.service.UserClient;
import lombok.SneakyThrows;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.List;
import java.util.stream.Stream;

import static guru.qa.niffler.common.utils.NifflerFaker.randomPassword;
import static guru.qa.niffler.common.utils.NifflerFaker.randomUserName;
import static guru.qa.niffler.config.Configuration.CFG;
import static org.apache.hc.core5.http.HttpStatus.SC_OK;

public class UserApiClient implements ApiClient, UserClient {
    private static final CookieManager COOKIE_MANAGER = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
    private final Retrofit userRetrofit = new Retrofit.Builder()
            .baseUrl(CFG.apiUserdataUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();
    private final Retrofit authRetrofit = new Retrofit.Builder()
            .baseUrl(CFG.apiAuthUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .client(new OkHttpClient.Builder()
                    .cookieJar(new JavaNetCookieJar(
                            COOKIE_MANAGER
                    ))
                    .build())
            .build();
    private final UserApi userApi = userRetrofit.create(UserApi.class);
    private final AuthApi authApi = authRetrofit.create(AuthApi.class);

    @Override
    @SneakyThrows
    public UserJson createUser(String username, String password) {
        authApi.requestRegisterForm().execute();
        authApi.register(
                username,
                password,
                password,
                COOKIE_MANAGER.getCookieStore().getCookies()
                        .stream()
                        .filter(c -> c.getName().equals("XSRF-TOKEN"))
                        .findFirst()
                        .get()
                        .getValue()
        ).execute();
        return UserJson.create(username);
    }

    @Override
    public List<UserJson> createIncomeInvitations(UserJson addressee, int count) {
        List<UserJson> requesters = createUsers(count);
        requesters.forEach(requester -> sendInvitation(requester.getUsername(), addressee.getUsername()));
        return requesters;
    }

    @Override
    public List<UserJson> createOutcomeInvitations(UserJson requester, int count) {
        List<UserJson> addresses = createUsers(count);
        addresses.forEach(addressee -> sendInvitation(requester.getUsername(), addressee.getUsername()));
        return addresses;
    }

    @Override
    public List<UserJson> createFriendShip(UserJson user, int count) {
        List<UserJson> friends = createUsers(count);
        friends.forEach(friend -> {
            sendInvitation(user.getUsername(), friend.getUsername());
            acceptInvitation(friend.getUsername(), user.getUsername());
        });
        return friends;
    }

    @SneakyThrows
    public UserJson sendInvitation(String username, String targetUsername) {
        return executeWithAssert(
                userApi.sendInvitation(username, targetUsername),
                SC_OK,
                "При отправке приглашения дружбы пользователю %s от пользователя %s возникла ошибка" .formatted(targetUsername, username)
        );
    }

    @SneakyThrows
    public UserJson acceptInvitation(String username, String targetUsername) {
        return executeWithAssert(
                userApi.acceptInvitation(username, targetUsername),
                SC_OK,
                "При отправке подтверждения дружбы пользователю %s от пользователя %s возникла ошибка" .formatted(targetUsername, username)
        );
    }

    private List<UserJson> createUsers(int count) {
        return Stream.generate(() -> createUser(randomUserName(), randomPassword()))
                .limit(count)
                .toList();
    }
}
