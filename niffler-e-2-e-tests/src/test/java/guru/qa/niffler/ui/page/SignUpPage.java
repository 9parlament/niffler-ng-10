package guru.qa.niffler.ui.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.ui.util.ValidationErrorChecker;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@Page(value = "/register", auth = true)
public class SignUpPage {
    private static final String SUCCESS_SIGN_UP_TEXT = "Congratulations! You've registered!";
    private final SelenideElement registerForm = $("#register-form");
    private final SelenideElement successSignUpForm = $("#form");
    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement passwordInput = $("#password");
    private final SelenideElement passwordSubmitInput = $("#passwordSubmit");
    private final SelenideElement toLoginPageLink = $(".form__link");
    private final SelenideElement registerButton = $("#register-button");

    @Step("Заполнить форму регистрации: логин {0} пароль {1} подтверждение {2}")
    public SignUpPage fillForm(String username, String password, String submissionPassword) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        passwordSubmitInput.setValue(submissionPassword);
        return this;
    }

    @Step("Нажать кнопку \"Sign Up\"")
    public SignUpPage signUp() {
        registerButton.click();
        return this;
    }

    @Step("Перейти на страницу \"Log in\" по ссылке")
    public LoginPage returnToLoginPage() {
        toLoginPageLink.click();
        return new LoginPage();
    }

    @Step("Проверить сообщение об успешной регистрации нового пользователя")
    public void checkSuccessSignUpMessage() {
        successSignUpForm.shouldHave(text(SUCCESS_SIGN_UP_TEXT));
    }

    @Step("Проверить, что страница регистрации открылась")
    public void checkPageIsOpened() {
        registerForm.shouldBe(visible)
                .$(".header").shouldHave(text("Sign up"));
    }

    public SignUpPage checkUsernameInputValidationError(String errorText) {
        ValidationErrorChecker.check(usernameInput, errorText);
        return this;
    }

    public SignUpPage checkPasswordInputValidationError(String errorText) {
        ValidationErrorChecker.check(passwordInput, errorText);
        return this;
    }

    public SignUpPage checkPasswordSubmitInputValidationError(String errorText) {
        ValidationErrorChecker.check(passwordSubmitInput, errorText);
        return this;
    }
}
