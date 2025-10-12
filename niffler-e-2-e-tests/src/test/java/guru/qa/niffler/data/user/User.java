package guru.qa.niffler.data.user;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class User {
    private final String username;
    private final String password;
    private final List<User> friends = new ArrayList<>();
    private final List<User> incomeInvitations = new ArrayList<>();
    private final List<User> outcomeInvitations = new ArrayList<>();

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
