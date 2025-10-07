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
    void validationErrShouldExistWhenSubmissionPasswordIsIncorrectTest() {
        String password = "pass";
        Browser.open(SignUpPage.class)
                .fillForm("user", password, password + "1")
                .signUp()
                .checkPasswordInputValidationError("Passwords should be equal")
                .checkPasswordSubmitInputValidationError(EMPTY);
    }

    @Test
    void successSignUpNewUserWithCorrectDataTest() {
        String username = NifflerFaker.getUserName();
        Browser.open(SignUpPage.class)
                .fillForm(username, "321", "321")
                .signUp()
                .checkSuccessSignUpMessage();
    }

    @Test
    void signUpShouldFailedWhenUserAlreadyExist() {
        String existedUser = DEFAULT_USER.getUsername();
        Browser.open(SignUpPage.class)
                .fillForm(existedUser, "123", "123")
                .signUp()
                .checkUsernameInputValidationError("Username `%s` already exists".formatted(existedUser));
    }

    @Test
    void loginPageShouldOpenWhenLoginLinkIsClickedTest() {
        Browser.open(SignUpPage.class)
                .returnToLoginPage()
                .checkPageIsOpened();
    }
}
