package guru.qa.niffler.test;

import guru.qa.niffler.ui.page.LoginPage;
import guru.qa.niffler.ui.core.Browser;
import io.qameta.allure.Step;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.model.User.DEFAULT_USER;

@DisplayName("Авторизация пользователя")
class LoginTests {

    @Test
    @DisplayName("Открытие страницы регистрации при нажатии кнопки \"Create New Account\"")
    void signUpPageShouldBeOpenWhenCreateNewAccountIsClickedTest() {
        Browser.open(LoginPage.class)
                .goToSignUpPage()
                .checkPageIsOpened();
    }

    @Test
    @DisplayName("Вход в систему с валидными данными существующего пользователя")
    void shouldSuccessLoginWhenValidDataSubmittedTest() {
        Browser.open(LoginPage.class)
                .login(DEFAULT_USER.getUsername(), DEFAULT_USER.getPassword())
                .checkIsOpened();
    }

    @Test
    @Step("Появление ошибки при попытке авторизации с невалидными данными")
    void badCredentialsErrorShouldPresentIfUserDataIncorrectTest() {
        Browser.open(LoginPage.class)
                .badLogin(DEFAULT_USER.getUsername(), "incorrectPass")
                .checkBadCredentialsError();
    }
}
