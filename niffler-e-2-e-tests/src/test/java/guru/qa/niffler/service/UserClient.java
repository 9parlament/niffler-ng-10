package guru.qa.niffler.service;

import guru.qa.niffler.model.api.UserJson;

public interface UserClient {

    UserJson createUser(String username, String password);

    void createIncomeInvitations(UserJson user, int count);

    void createOutcomeInvitations(UserJson user, int count);

    void createFriendShip(UserJson user, int count);
}
