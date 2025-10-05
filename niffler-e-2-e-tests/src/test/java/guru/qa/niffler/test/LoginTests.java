package guru.qa.niffler.test;

import guru.qa.niffler.jupiter.extension.DefaultUserCreationExtension;
import guru.qa.niffler.ui.page.LoginPage;
import guru.qa.niffler.ui.core.Browser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.model.User.DEFAULT_USER;

@ExtendWith(DefaultUserCreationExtension.class)
@DisplayName("Авторизация пользователя")
class LoginTests {

    @Test
    @DisplayName("Открытие страницы регистрации при нажатии кнопки \"Create New Account\"")
    void signUpPageShouldBeOpenWhenCreateNewAccountIsClicked() {
        Browser.open(LoginPage.class)
                .goToSignUpPage()
                .checkPageIsOpened();
    }

    @Test
    void shouldSuccessLoginWhenValidDataSubmitted() {
        Browser.open(LoginPage.class)
                .login(DEFAULT_USER.getUsername(), DEFAULT_USER.getPassword())
                .checkIsOpened();
    }
}
