package guru.qa.niffler.model.entity;

import jakarta.persistence.CascadeType;
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
@Table(name = "\"user\"")
@Accessors(chain = true)
public class AuthUserEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private String username;
    private String password;
    private Boolean enabled;
    private Boolean accountNonExpired;
    private Boolean accountNonLocked;
    private Boolean credentialsNonExpired;
    @OneToMany(mappedBy = "authUser", cascade = CascadeType.ALL)
    private List<AuthorityEntity> authorities;

    public static AuthUserEntity create(String username, String password) {
        return new AuthUserEntity()
                .setUsername(username)
                .setPassword(password)
                .setEnabled(true)
                .setAccountNonExpired(true)
                .setAccountNonLocked(true)
                .setCredentialsNonExpired(true)
                .setAuthorities(new ArrayList<>());
    }
}
