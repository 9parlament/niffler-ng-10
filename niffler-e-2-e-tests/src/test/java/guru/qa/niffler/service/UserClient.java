package guru.qa.niffler.service;

import guru.qa.niffler.model.api.UserJson;

import java.util.List;

public interface UserClient {

    UserJson createUser(String username, String password);

    List<UserJson> createIncomeInvitations(UserJson user, int count);

    List<UserJson> createOutcomeInvitations(UserJson user, int count);

    List<UserJson> createFriendShip(UserJson user, int count);
}
