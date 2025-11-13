package guru.qa.niffler.model.test;

import guru.qa.niffler.model.api.CategoryJson;
import guru.qa.niffler.model.api.SpendJson;
import guru.qa.niffler.model.api.UserJson;

import java.util.List;

public record TestData(String password,
                       List<UserJson> incomeInvitations,
                       List<UserJson> outcomeInvitations,
                       List<UserJson> friends,
                       List<CategoryJson> categories,
                       List<SpendJson> spends) {
}
