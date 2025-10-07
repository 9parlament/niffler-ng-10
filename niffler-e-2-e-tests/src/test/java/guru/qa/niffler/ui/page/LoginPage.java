package guru.qa.niffler.ui.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@Page(value = "/login", auth = true)
public class LoginPage {
    private static final String BAD_CREDENTIALS_ERROR = "Неверные учетные данные пользователя";
    private final SelenideElement loginForm = $("#login-form");
    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement passwordInput = $("#password");
    private final SelenideElement submitBtn = $("#login-button");
    private final SelenideElement registerButton = $("#register-button");

    @Step("Авторизоваться в системе, используя валидные логин {0} и пароль {1}")
    public MainPage login(String username, String password) {
        usernameInput.val(username);
        passwordInput.val(password);
        submitBtn.click();
        return new MainPage();
    }

    @Step("Ввести невалидные логин {0} и пароль {1}")
    public LoginPage badLogin(String username, String password) {
        usernameInput.val(username);
        passwordInput.val(password);
        submitBtn.click();
        return new LoginPage();
    }

    @Step("Проверить, что страница авторизации открылась")
    public void checkPageIsOpened() {
        loginForm.shouldBe(Condition.visible)
                .$(".header").shouldHave(text("Log in"));
    }

    @Step("Перейти на страницу регистрации по кнопке \"Create new account\"")
    public SignUpPage goToSignUpPage() {
        registerButton.click();
        return new SignUpPage();
    }

    @Step("Проверить наличие ошибки о невалидных авторизационных данных")
    public LoginPage checkBadCredentialsError() {
        loginForm.parent().$(".form__error")
                .shouldBe(visible).shouldHave(text(BAD_CREDENTIALS_ERROR));
        return this;
    }
}
