package guru.qa.niffler.service;

import guru.qa.niffler.model.api.CategoryJson;
import guru.qa.niffler.model.api.SpendJson;

import java.util.List;

public interface SpendClient {

    SpendJson createSpend(SpendJson spend);

    SpendJson editSpend(SpendJson spend);

    void deleteSpend(SpendJson spend);

    List<SpendJson> getSpendsByDescription(String username, String description);

    CategoryJson createCategory(CategoryJson category);

    CategoryJson getCategoryByName(String username, String name);

    void deleteCategory(CategoryJson category);
}
