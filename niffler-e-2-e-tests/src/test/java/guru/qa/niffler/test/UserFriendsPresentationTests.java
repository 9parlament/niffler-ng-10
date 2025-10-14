package guru.qa.niffler.test;

import guru.qa.niffler.data.user.User;
import guru.qa.niffler.jupiter.annotation.UserT;
import guru.qa.niffler.jupiter.extension.UserQueueExtension;
import guru.qa.niffler.ui.core.Browser;
import guru.qa.niffler.ui.page.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.data.user.UserType.EMPTY;
import static guru.qa.niffler.data.user.UserType.WITH_FRIEND;
import static guru.qa.niffler.data.user.UserType.WITH_INCOME_REQUEST;
import static guru.qa.niffler.data.user.UserType.WITH_OUTCOME_REQUEST;


@ExtendWith(UserQueueExtension.class)
@DisplayName("Отображение запросов о дружбе и списка друзей пользователя")
class UserFriendsPresentationTests {

    @Test
    @DisplayName("Исходящие запросы дружбы должны быть на странице всех пользователей системы")
    void outcomeInvitationsShouldBePresentOnAllPeopleTabTest(@UserT(WITH_OUTCOME_REQUEST) User user) {
        Browser.open(LoginPage.class)
                .login(user.getUsername(), user.getPassword())
                .goToPeoplePage()
                .checkThatOutcomeInvitationsExists(user.getOutcomeInvitations());
    }

    @Test
    @DisplayName("Входящие запросы дружбы должны быть на странице друзей пользователя")
    void incomeInvitationShouldBePresentInFriendRequestsTest(@UserT(WITH_INCOME_REQUEST) User user) {
        Browser.open(LoginPage.class)
                .login(user.getUsername(), user.getPassword())
                .goToFriendsPage()
                .checkThatIncomeInvitationsExists(user.getIncomeInvitations());
    }

    @Test
    @DisplayName("Друзья должны быть на странице друзей пользователя")
    void friendsShouldBePresentInMyFriendsListTest(@UserT(WITH_FRIEND) User user) {
        Browser.open(LoginPage.class)
                .login(user.getUsername(), user.getPassword())
                .goToFriendsPage()
                .checkThatFriendsExists(user.getFriends());

    }

    @Test
    @DisplayName("Список друзей должен быть пуст, если у пользователя нет друзей")
    void friendsListShouldBeEmptyIfUserHasNotFriendsTest(@UserT(EMPTY) User user) {
        Browser.open(LoginPage.class)
                .login(user.getUsername(), user.getPassword())
                .goToFriendsPage()
                .checkThatFriendsNoExists();
    }
}
