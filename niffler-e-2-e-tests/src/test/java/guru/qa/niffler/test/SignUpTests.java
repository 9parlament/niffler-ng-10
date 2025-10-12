package guru.qa.niffler.test;

import guru.qa.niffler.common.utils.NifflerFaker;
import guru.qa.niffler.ui.core.Browser;
import guru.qa.niffler.ui.page.SignUpPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.model.User.DEFAULT_USER;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@DisplayName("Регистрация нового пользователя")
class SignUpTests {

    @Test
    @DisplayName("Появление ошибки валидации поля ввода, если подтверждение пароля не успешно")
    void validationErrShouldExistWhenSubmissionPasswordIsIncorrectTest() {
        String password = "pass";
        Browser.open(SignUpPage.class)
                .fillForm("user", password, password + "1")
                .signUp()
                .checkPasswordInputValidationError("Passwords should be equal")
                .checkPasswordSubmitInputValidationError(EMPTY);
    }

    @Test
    @DisplayName("Успешная регистрация пользователя с валидными данными")
    void successSignUpNewUserWithCorrectDataTest() {
        String username = NifflerFaker.randomUserName();
        Browser.open(SignUpPage.class)
                .fillForm(username, "321", "321")
                .signUp()
                .checkSuccessSignUpMessage();
    }

    @Test
    @DisplayName("Ошибка создания нового пользователя, если он уже имеется в системе")
    void signUpShouldFailedWhenUserAlreadyExist() {
        String existedUser = DEFAULT_USER.getUsername();
        Browser.open(SignUpPage.class)
                .fillForm(existedUser, "123", "123")
                .signUp()
                .checkUsernameInputValidationError("Username `%s` already exists".formatted(existedUser));
    }

    @Test
    @DisplayName("Открытие страницы авторизации при переходе по ссылке \"Log in\"")
    void loginPageShouldOpenWhenLoginLinkIsClickedTest() {
        Browser.open(SignUpPage.class)
                .returnToLoginPage()
                .checkPageIsOpened();
    }
}
