package guru.qa.niffler.test;

import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.ui.core.Browser;
import guru.qa.niffler.ui.page.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.model.User.DEFAULT_USER;

@DisplayName("Отображение трат на главной странице")
class SpendingPresentationTests {

    @Test
    @User
    @DisplayName("История трат пуста, если у пользователя за всё время не было ни одной траты")
    void historyOfSpendingsShouldBeEmptyIfUserHasNotSpengingsTest() {
        Browser.open(LoginPage.class)
                .login(DEFAULT_USER.getUsername(), DEFAULT_USER.getPassword())
                .checkThatHistoryOfSpendingsIsEmpty();
    }

    @Test
    @User(spending = @Spending(
            category = "Кофе",
            description = "Кофе с собой",
            amount = 350
    ))
    @DisplayName("Трата пользователя отображается в истории трат")
    void spendingShouldBePresentOnHistoryOfSpendingsIfThatExistTest(SpendJson spending) {
        Browser.open(LoginPage.class)
                .login(DEFAULT_USER.getUsername(), DEFAULT_USER.getPassword())
                .checkThatSpendingExistInHistoryOfSpendings(spending);
    }
}
