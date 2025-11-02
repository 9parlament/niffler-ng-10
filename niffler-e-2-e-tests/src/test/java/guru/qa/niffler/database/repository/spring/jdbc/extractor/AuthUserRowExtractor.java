package guru.qa.niffler.database.repository.spring.jdbc.extractor;

import guru.qa.niffler.model.entity.AuthUserEntity;
import guru.qa.niffler.model.entity.AuthorityEntity;
import lombok.NoArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class AuthUserRowExtractor implements ResultSetExtractor<AuthUserEntity> {
    public static final AuthUserRowExtractor INSTANCE = new AuthUserRowExtractor();

    @Override
    public AuthUserEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<UUID, AuthUserEntity> extractedUsers = new HashMap<>();
        AuthUserEntity authUser = null;
        while (rs.next()) {
            UUID id = rs.getObject("id", UUID.class);
            if (!extractedUsers.containsKey(id)) {
                extractedUsers.put(
                        id,
                        new AuthUserEntity()
                                .setId(id)
                                .setUsername(rs.getString("username"))
                                .setPassword(rs.getString("password"))
                                .setEnabled(rs.getBoolean("enabled"))
                                .setAccountNotExpired(rs.getBoolean("account_non_expired"))
                                .setAccountNonLocked(rs.getBoolean("account_non_locked"))
                                .setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"))
                                .setAuthorities(new ArrayList<>()));
            }
            authUser = extractedUsers.get(id);
            AuthorityEntity authority = new AuthorityEntity()
                    .setId(rs.getObject("authority_id", UUID.class))
                    .setAuthority(rs.getString("authority_authority"))
                    .setAuthUser(authUser);
            authUser.getAuthorities().add(authority);
        }
        return authUser;
    }
}
