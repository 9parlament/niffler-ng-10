package guru.qa.niffler.ui.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@Page("/main")
public class MainPage {
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
}
