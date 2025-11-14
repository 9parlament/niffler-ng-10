package guru.qa.niffler.api.rest;

import guru.qa.niffler.api.rest.spec.SpendApi;
import guru.qa.niffler.model.api.CategoryJson;
import guru.qa.niffler.model.api.SpendJson;
import guru.qa.niffler.service.SpendClient;
import lombok.SneakyThrows;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static guru.qa.niffler.config.Configuration.CFG;
import static guru.qa.niffler.model.CurrencyValues.RUB;
import static org.apache.hc.core5.http.HttpStatus.SC_ACCEPTED;
import static org.apache.hc.core5.http.HttpStatus.SC_CREATED;
import static org.apache.hc.core5.http.HttpStatus.SC_OK;

public class SpendApiClient implements ApiClient, SpendClient {
    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CFG.apiSpendUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();
    private final SpendApi spendApi = retrofit.create(SpendApi.class);

    @Override
    public SpendJson createSpend(SpendJson newSpend) {
        return executeWithAssert(
                spendApi.createSpend(newSpend),
                SC_CREATED,
                "При создании траты возникла непредвиденная ошибка"
        );
    }

    @Override
    public SpendJson editSpend(SpendJson editedSpend) {
        return executeWithAssert(
                spendApi.editSpend(editedSpend),
                SC_OK,
                "При изменении траты возникла непредвиденная ошибка"
        );
    }

    @Override
    public void deleteSpend(SpendJson spend) {
        executeWithAssert(
                spendApi.removeSpend(spend.getUsername(), List.of(spend.getId().toString())),
                SC_ACCEPTED,
                "При удалении траты возникла непредвиденная ошибка"
        );
    }

    @Override
    @SneakyThrows
    public List<SpendJson> getSpendsByDescription(String username, String description) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = sdf.parse("2000-01-01");
        Date endDate = sdf.parse(sdf.format(new Date()));

        List<SpendJson> allUserSpends = executeWithAssert(
                spendApi.getSpends(username, RUB, startDate, endDate),
                SC_OK,
                "При запросе информации о тратах возникла непредвиденная ошибка"
        );

        return allUserSpends.stream()
                .filter(spend -> spend.getUsername().equals(username))
                .filter(spend -> spend.getDescription().equals(description))
                .collect(Collectors.toList());
    }

    @Override
    public CategoryJson createCategory(CategoryJson newCategory) {
        return executeWithAssert(spendApi.createCategory(newCategory),
                SC_OK,
                "При создании категории возникла непредвиденная ошибка"
        );
    }

    @Override
    public CategoryJson getCategoryByName(String username, String name) {
        return executeWithAssert(
                spendApi.getCategories(username, false),
                SC_OK,
                "При запросе информации о категориях возникла непредвиденная ошибка"
        ).stream()
                .filter(category -> category.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void deleteCategory(CategoryJson category) {
        throw new UnsupportedOperationException("Метод не реализован");
    }
}
