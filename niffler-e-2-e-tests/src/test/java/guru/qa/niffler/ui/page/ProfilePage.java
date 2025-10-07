package guru.qa.niffler.ui.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@Page("/profile")
public class ProfilePage {
    private final ElementsCollection categoryItems = $$(".MuiChip-label");
    private final SelenideElement showArchivedRadioButton = $(".MuiFormControlLabel-root");

    @Step("Вынести категорию {0} в архив")
    public ProfilePage archiveCategory(String categoryName) {
        findCategory(categoryName).$("[aria-label='Archive category']").click();
        $$(".MuiDialogActions-root button").findBy(text("Archive")).click();
        return this;
    }

    @Step("Активировать опцию \"Show archived\"")
    public ProfilePage showArchivedCategories() {
        showArchivedRadioButton.click();
        return this;
    }

    @Step("Вынести категорию {0} из архива и сделать активной")
    public ProfilePage unarchiveCategory(String categoryName) {
        findCategory(categoryName).$("[aria-label='Unarchive category']").click();
        $$(".MuiDialogActions-root button").findBy(text("Unarchive")).click();
        return this;
    }

    @Step("Проверить, что категория {0} есть в списке и является архивной")
    public ProfilePage checkCategoryIsArchived(String categoryName) {
        findCategory(categoryName).$("[aria-label='Edit category']").shouldNot(exist);
        return this;
    }

    @Step("Проверить, что категория {0} есть в списке и является активной")
    public ProfilePage checkCategoryIsActive(String categoryName) {
        findCategory(categoryName).$("[aria-label='Edit category']").shouldBe(visible);
        return this;
    }

    @Step("Проверить, что категория {0} не отображается в списке")
    public ProfilePage checkCategoryIsNotPresent(String categoryName) {
        findCategory(categoryName).shouldNot(exist);
        return this;
    }

    private SelenideElement findCategory(String categoryName) {
        return categoryItems.findBy(text(categoryName)).closest(".MuiGrid-root");
    }
}
