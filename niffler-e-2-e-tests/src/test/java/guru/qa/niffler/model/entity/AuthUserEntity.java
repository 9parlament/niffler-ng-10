package guru.qa.niffler.model.entity;

import guru.qa.niffler.model.test.user.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
public class AuthUserEntity {
    private UUID id;
    private String username;
    private String password;
    private Boolean enabled;
    private Boolean accountNotExpired;
    private Boolean accountNonLocked;
    private Boolean credentialsNonExpired;

    public static AuthUserEntity fromTestUser(User user) {
        return new AuthUserEntity()
                .setUsername(user.getUsername())
                .setPassword(user.getPassword())
                .setEnabled(true)
                .setAccountNotExpired(true)
                .setAccountNonLocked(true)
                .setCredentialsNonExpired(true);
    }
}
