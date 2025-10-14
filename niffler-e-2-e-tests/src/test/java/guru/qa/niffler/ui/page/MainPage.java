package guru.qa.niffler.ui.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@Page("/main")
public class MainPage {
    private final ElementsCollection userMenuOptions = $$("[role=menuitem]");
    private final SelenideElement pageHeader = $("#root .MuiPaper-root");
    private final SelenideElement spendingTable = $("#spendings");
    private final SelenideElement statBlock = $("#stat");

    @Step("Проверить, что главная страница открылась")
    public MainPage checkIsOpened() {
        pageHeader.shouldBe(visible);
        spendingTable.shouldBe(visible);
        statBlock.shouldBe(visible);
        return this;
    }

    @Step("Перейти на страницу профиля пользователя")
    public ProfilePage goToProfilePage() {
        openUserActionsMenu();
        userMenuOptions.findBy(text("Profile")).click();
        return new ProfilePage();
    }

    @Step("Перейти на страницу отображения всех пользователей системы")
    public PeoplePage goToPeoplePage() {
        openUserActionsMenu();
        userMenuOptions.findBy(text("All People")).click();
        return new PeoplePage();
    }

    @Step("Перейти на страницу отображения друзей пользователя")
    public PeoplePage goToFriendsPage() {
        openUserActionsMenu();
        userMenuOptions.findBy(text("Friends")).click();
        return new PeoplePage();
    }

    private void openUserActionsMenu() {
        pageHeader.$("button").click();
    }
}
