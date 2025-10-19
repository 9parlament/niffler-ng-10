package guru.qa.niffler.ui.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.api.CategoryJson;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

//TODO: подумать над реализацией
@Page("/spending/spendID")
public class EditSpendingPage {
    private final SelenideElement selectedCategory = $("#category");
    private final SelenideElement saveChangesButton = $("#save");
    private final ElementsCollection activeCategories = $$("ul.MuiList-root li");

    @Step("Выбрать категорию из списка активных категорий")
    public EditSpendingPage selectCategoryFromAvailableActive(CategoryJson category) {
        activeCategories.as("список активных категорий").findBy(text(category.getName())).click();
        selectedCategory.shouldHave(value(category.getName()));
        return this;
    }

    @Step("Нажать кнопку \"Save changes\" и перейти на главную страницу")
    public MainPage saveChanges() {
        saveChangesButton.click();
        return new MainPage();
    }
}
