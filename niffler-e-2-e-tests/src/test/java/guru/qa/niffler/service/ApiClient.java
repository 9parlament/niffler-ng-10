package guru.qa.niffler.service;

import org.junit.jupiter.api.Assertions;
import retrofit2.Call;
import retrofit2.Response;

public interface ApiClient {

    default <T> T executeWithAssert(Call<T> call, int expectedStatus, String errorMessage) {
        final Response<T> response;
        try {
            response = call.execute();
        } catch (Exception e) {
            throw new RuntimeException(errorMessage, e);
        }
        Assertions.assertEquals(expectedStatus, response.code());
        return response.body();
    }
}
