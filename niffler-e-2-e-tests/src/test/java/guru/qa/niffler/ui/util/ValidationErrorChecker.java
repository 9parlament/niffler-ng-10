package guru.qa.niffler.ui.util;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Allure;
import lombok.NoArgsConstructor;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@NoArgsConstructor(access = PRIVATE)
public class ValidationErrorChecker {

    public static void check(SelenideElement formInput, String errorText) {
        String fieldName = formInput.getSearchCriteria().replaceFirst("^by css: #", "");
        SelenideElement validationError = formInput.parent().$(".form__error");
        if (isNotEmpty(errorText)) {
            Allure.step("Проверить, что ошибка валидации в поле %s содержит текст %s".formatted(fieldName, errorText),
                    () -> validationError.shouldBe(visible).shouldHave(text(errorText)));
        } else {
            Allure.step("Проверить, что ошибка валидации в поле %s отсутствует".formatted(fieldName),
                    () -> validationError.shouldNotBe(visible));
        }
    }
}
