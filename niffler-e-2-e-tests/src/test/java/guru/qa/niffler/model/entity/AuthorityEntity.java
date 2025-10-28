package guru.qa.niffler.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
public class AuthorityEntity {
    private UUID id;
    private UUID userId;
    private String authority;

    public static AuthorityEntity readAuthority(AuthUserEntity user) {
        return new AuthorityEntity()
                .setUserId(user.getId())
                .setAuthority("read");
    }

    public static AuthorityEntity writeAuthority(AuthUserEntity user) {
        return new AuthorityEntity()
                .setUserId(user.getId())
                .setAuthority("write");
    }
}
