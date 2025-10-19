package guru.qa.niffler.data.user;

import guru.qa.niffler.common.utils.NifflerFaker;
import guru.qa.niffler.api.rest.AuthApiClient;
import guru.qa.niffler.api.rest.UserApiClient;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import static guru.qa.niffler.data.user.UserType.EMPTY;
import static guru.qa.niffler.data.user.UserType.WITH_FRIEND;
import static guru.qa.niffler.data.user.UserType.WITH_INCOME_REQUEST;
import static guru.qa.niffler.data.user.UserType.WITH_OUTCOME_REQUEST;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class UserFactory {
    private static final Map<UserType, Integer> CONFIG = Map.of(
            EMPTY, 1,
            WITH_FRIEND, 1,
            WITH_INCOME_REQUEST, 1,
            WITH_OUTCOME_REQUEST, 1
    );
    private static final AuthApiClient AUTH_API_CLIENT = new AuthApiClient();
    private static final UserApiClient USER_API_CLIENT = new UserApiClient();

    public static Map<UserType, Queue<User>> createUsersQueueByConfig() {
        return CONFIG.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> switch (entry.getKey()) {
                            case EMPTY -> createEmptyUserQueue(entry.getValue());
                            case WITH_FRIEND -> createWithFriendUserQueue(entry.getValue());
                            case WITH_INCOME_REQUEST -> createWithIncomeReqUserQueue(entry.getValue());
                            case WITH_OUTCOME_REQUEST -> createWithOutcomeReqUserQueue(entry.getValue());
                        }
                ));
    }

    @SneakyThrows
    private static Queue<User> createEmptyUserQueue(int count) {
        Queue<User> emptyUsers = new ConcurrentLinkedQueue<>();
        for (int i = 0; i < count; i++) {
            User user = new User(NifflerFaker.randomUserName(), NifflerFaker.randomPassword());
            AUTH_API_CLIENT.register(user.getUsername(), user.getPassword());
            emptyUsers.add(user);
        }
        return emptyUsers;
    }

    private static Queue<User> createWithFriendUserQueue(int count) {
        Queue<User> usersWithFriend = new ConcurrentLinkedQueue<>();
        for (int i = 0; i < count; i++) {
            UserPair pair = createUserPair();
            USER_API_CLIENT.sendInvitation(pair.user.getUsername(), pair.friend.getUsername());
            USER_API_CLIENT.acceptInvitation(pair.friend.getUsername(), pair.user.getUsername());

            pair.user.getFriends().add(pair.friend);
            pair.friend.getFriends().add(pair.user);
            usersWithFriend.add(pair.user);
        }
        return usersWithFriend;
    }

    private static Queue<User> createWithIncomeReqUserQueue(int count) {
        Queue<User> usersWithIncomeReq = new ConcurrentLinkedQueue<>();
        for (int i = 0; i < count; i++) {
            UserPair pair = createUserPair();
            USER_API_CLIENT.sendInvitation(pair.user().getUsername(), pair.friend().getUsername());

            pair.user().getOutcomeInvitations().add(pair.friend());
            pair.friend().getIncomeInvitations().add(pair.user());
            usersWithIncomeReq.add(pair.user);
        }
        return usersWithIncomeReq;
    }

    private static Queue<User> createWithOutcomeReqUserQueue(int count) {
        Queue<User> usersWithOutcomeReq = new ConcurrentLinkedQueue<>();
        for (int i = 0; i < count; i++) {
            UserPair pair = createUserPair();
            USER_API_CLIENT.sendInvitation(pair.user.getUsername(), pair.friend.getUsername());

            pair.user.getIncomeInvitations().add(pair.friend);
            pair.friend.getOutcomeInvitations().add(pair.user);
            usersWithOutcomeReq.add(pair.user);
        }
        return usersWithOutcomeReq;
    }

    @SneakyThrows
    private static UserPair createUserPair() {
        var user = new User(NifflerFaker.randomUserName(), NifflerFaker.randomPassword());
        AUTH_API_CLIENT.register(user.getUsername(), user.getPassword());
        var friend = new User(NifflerFaker.randomUserName(), NifflerFaker.randomPassword());
        AUTH_API_CLIENT.register(friend.getUsername(), friend.getPassword());
        return new UserPair(user, friend);
    }

    private record UserPair(User user, User friend) {
    }
}
