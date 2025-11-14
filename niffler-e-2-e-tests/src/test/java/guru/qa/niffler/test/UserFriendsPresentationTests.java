package guru.qa.niffler.test;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.api.UserJson;
import guru.qa.niffler.ui.core.Browser;
import guru.qa.niffler.ui.page.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.jupiter.annotation.UserType.RANDOM;

@DisplayName("Отображение запросов о дружбе и списка друзей пользователя")
class UserFriendsPresentationTests {

    @Test
    @User(user = RANDOM, outcomeInvitations = 2)
    @DisplayName("Исходящие запросы дружбы должны быть на странице всех пользователей системы")
    void outcomeInvitationsShouldBePresentOnAllPeopleTabTest(UserJson user) {
        Browser.open(LoginPage.class)
                .login(user.getUsername(), user.getTestData().password())
                .goToPeoplePage()
                .checkThatOutcomeInvitationsExists(user.getTestData().outcomeInvitations());
    }

    @Test
    @User(user = RANDOM, incomeInvitations = 2)
    @DisplayName("Входящие запросы дружбы должны быть на странице друзей пользователя")
    void incomeInvitationShouldBePresentInFriendRequestsTest(UserJson user) {
        Browser.open(LoginPage.class)
                .login(user.getUsername(), user.getTestData().password())
                .goToFriendsPage()
                .checkThatIncomeInvitationsExists(user.getTestData().incomeInvitations());
    }

    @Test
    @User(user = RANDOM, friends = 2)
    @DisplayName("Друзья должны быть на странице друзей пользователя")
    void friendsShouldBePresentInMyFriendsListTest(UserJson user) {
        Browser.open(LoginPage.class)
                .login(user.getUsername(), user.getTestData().password())
                .goToFriendsPage()
                .checkThatFriendsExists(user.getTestData().friends());

    }

    @Test
    @User(user = RANDOM)
    @DisplayName("Список друзей должен быть пуст, если у пользователя нет друзей")
    void friendsListShouldBeEmptyIfUserHasNotFriendsTest(UserJson user) {
        Browser.open(LoginPage.class)
                .login(user.getUsername(), user.getTestData().password())
                .goToFriendsPage()
                .checkThatFriendsNoExists();
    }
}
