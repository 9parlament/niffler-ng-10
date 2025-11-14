package guru.qa.niffler.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "AUTHORITY")
@Accessors(chain = true)
public class AuthorityEntity {
    @Id
    @GeneratedValue
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private AuthUserEntity authUser;
    private String authority;

    public static AuthorityEntity readAuthority(AuthUserEntity user) {
        return new AuthorityEntity()
                .setAuthUser(user)
                .setAuthority("read");
    }

    public static AuthorityEntity writeAuthority(AuthUserEntity user) {
        return new AuthorityEntity()
                .setAuthUser(user)
                .setAuthority("write");
    }
}
