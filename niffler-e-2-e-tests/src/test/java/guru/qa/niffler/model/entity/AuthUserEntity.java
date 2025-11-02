package guru.qa.niffler.model.entity;

import guru.qa.niffler.model.test.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "USER")
@Accessors(chain = true)
public class AuthUserEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private String username;
    private String password;
    private Boolean enabled;
    private Boolean accountNotExpired;
    private Boolean accountNonLocked;
    private Boolean credentialsNonExpired;
    @OneToMany(mappedBy = "userId")
    private List<AuthorityEntity> authorities;

    public static AuthUserEntity fromTestUser(User user) {
        return new AuthUserEntity()
                .setUsername(user.getUsername())
                .setPassword(user.getPassword())
                .setEnabled(true)
                .setAccountNotExpired(true)
                .setAccountNonLocked(true)
                .setCredentialsNonExpired(true)
                .setAuthorities(new ArrayList<>());
    }
}
