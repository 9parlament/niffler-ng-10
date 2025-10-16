package guru.qa.niffler.ui.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.SpendJson;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static guru.qa.niffler.ui.condition.NifflerCondition.amount;
import static guru.qa.niffler.ui.condition.NifflerCondition.date;

@Page("/main")
public class MainPage {
    private final static String SPENDING_ROW_S = "#enhanced-table-checkbox-%s";
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

    @Step("Проверить, что список трат пуст")
    public MainPage checkThatHistoryOfSpendingsIsEmpty() {
        spendingTable.find("p.MuiTypography-root")
                .as("текст панели для списка трат")
                .shouldBe(visible)
                .shouldHave(text("There are no spendings"));
        $("[alt='Lonely niffler']")
                .as("логотип с niffler")
                .shouldBe(visible);
        return this;
    }

    @Step("Проверить, что трата отображается в истории трат пользователя")
    public MainPage checkThatSpendingExistInHistoryOfSpendings(SpendJson spending) {
        ElementsCollection spendingRow = $(SPENDING_ROW_S.formatted(spending.getId()))
                .closest("tr")
                .$$("span.MuiTypography-root");
        spendingRow.get(0).as("ячейка Category").shouldHave(text(spending.getCategory().getName()));
        spendingRow.get(1).as("ячейка Amount").shouldHave(amount(spending.getAmount(), spending.getCurrency()));
        spendingRow.get(2).as("ячейка Description").shouldHave(text(spending.getDescription()));
        spendingRow.get(3).as("ячейка Date").shouldHave(date(spending.getSpendDate()));
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
