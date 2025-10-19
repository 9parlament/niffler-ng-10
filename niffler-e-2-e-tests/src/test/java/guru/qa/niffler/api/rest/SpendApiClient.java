package guru.qa.niffler.api.rest;

import guru.qa.niffler.api.rest.spec.SpendApi;
import guru.qa.niffler.model.api.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.api.SpendJson;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.Date;
import java.util.List;

import static guru.qa.niffler.config.Configuration.CFG;
import static org.apache.hc.core5.http.HttpStatus.SC_ACCEPTED;
import static org.apache.hc.core5.http.HttpStatus.SC_CREATED;
import static org.apache.hc.core5.http.HttpStatus.SC_OK;

public class SpendApiClient implements ApiClient {
    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CFG.apiSpendUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();
    private final SpendApi spendApi = retrofit.create(SpendApi.class);

    public SpendJson createSpend(SpendJson newSpend) {
        return executeWithAssert(
                spendApi.createSpend(newSpend),
                SC_CREATED,
                "При создании траты возникла непредвиденная ошибка"
        );
    }

    public SpendJson editSpend(SpendJson editedSpend) {
        return executeWithAssert(
                spendApi.editSpend(editedSpend),
                SC_OK,
                "При изменении траты возникла непредвиденная ошибка"
        );
    }

    public void removeSpend(List<String> ids, String username) {
        executeWithAssert(
                spendApi.removeSpend(username, ids),
                SC_ACCEPTED,
                "При удалении траты возникла непредвиденная ошибка"
        );
    }

    public SpendJson getSpend(String username, String id) {
        return executeWithAssert(
                spendApi.getSpend(username, id),
                SC_OK,
                "При запросе информации о трате возникла непредвиденная ошибка"
        );
    }

    public List<SpendJson> getSpends(String username, Date from, Date to, CurrencyValues currency) {
        return executeWithAssert(
                spendApi.getSpends(username, currency, from, to),
                SC_OK,
                "При запросе информации о тратах возникла непредвиденная ошибка"
        );
    }

    public List<CategoryJson> getCategories(String username, boolean excludeArchived) {
        return executeWithAssert(
                spendApi.getCategories(username, excludeArchived),
                SC_OK,
                "При запросе информации о категориях возникла непредвиденная ошибка"
        );
    }

    public CategoryJson createCategory(CategoryJson newCategory) {
        return executeWithAssert(spendApi.createCategory(newCategory),
                SC_OK,
                "При создании категории возникла непредвиденная ошибка"
        );
    }

    public CategoryJson updateCategory(CategoryJson updatedCategory) {
        return executeWithAssert(spendApi.updateCategory(updatedCategory),
                SC_OK,
                "При обновлении категории возникла непредвиденная ошибка"
        );
    }
}
