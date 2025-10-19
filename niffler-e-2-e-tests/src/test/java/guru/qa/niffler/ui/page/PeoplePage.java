package guru.qa.niffler.ui.page;

import com.codeborne.selenide.ElementsCollection;
import guru.qa.niffler.model.test.user.User;
import io.qameta.allure.Step;

import java.util.List;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@Page("/people/all")
public class PeoplePage {
    private final ElementsCollection peopleRows = $$("tr");

    @Step("Проверить, что у пользователя отображаются исходящие запросы дружбы")
    public PeoplePage checkThatOutcomeInvitationsExists(List<User> outcomeInvitations) {
        outcomeInvitations.forEach(
                user -> {
                    peopleRows.findBy(text(user.getUsername()))
                            .$("span.MuiChip-label")
                            .as("метка об исходящем запросе дружбы")
                            .should(exist)
                            .shouldHave(text("Waiting..."));
                }
        );
        return this;
    }

    @Step("Проверить, что у пользователя отображаются входящие запросы дружбы")
    public PeoplePage checkThatIncomeInvitationsExists(List<User> incomeInvitations) {
        incomeInvitations.forEach(
                user -> {
                    peopleRows.findBy(text(user.getUsername()))
                            .$$("span.MuiTouchRipple-root")
                            .findBy(text("Accept"))
                            .as("кнопка принятия входящего запроса дружбы")
                            .should(exist)
                            .shouldBe(visible);
                }
        );
        return this;
    }

    @Step("Проверить, что у пользователя отображаются его друзья")
    public PeoplePage checkThatFriendsExists(List<User> friends) {
        friends.forEach(
                user -> {
                    peopleRows.findBy(text(user.getUsername()))
                            .$$("button.MuiButtonBase-root")
                            .findBy(text("Unfriend"))
                            .as("кнопка о прекращении дружбы")
                            .should(exist)
                            .shouldBe(visible);
                }
        );
        return this;
    }

    @Step("Проверить, что у пользователя отсутствуют друзья")
    public PeoplePage checkThatFriendsNoExists() {
        $("#simple-tabpanel-friends").shouldBe(visible)
                .find("p.MuiTypography-root")
                .as("текст панели для списка друзей")
                .shouldBe(visible)
                .shouldHave(text("There are no users yet"));
        $("[alt='Lonely niffler']")
                .as("логотип с niffler")
                .shouldBe(visible);
        return this;
    }

}
