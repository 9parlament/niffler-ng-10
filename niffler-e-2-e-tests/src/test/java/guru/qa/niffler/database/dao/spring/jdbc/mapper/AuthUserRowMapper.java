package guru.qa.niffler.database.dao.spring.jdbc.mapper;

import guru.qa.niffler.model.entity.AuthUserEntity;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class AuthUserRowMapper implements RowMapper<AuthUserEntity> {
    public static final AuthUserRowMapper INSTANCE = new AuthUserRowMapper();

    @Override
    public AuthUserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new AuthUserEntity()
                .setId(rs.getObject("id", UUID.class))
                .setUsername(rs.getString("username"))
                .setPassword(rs.getString("password"))
                .setEnabled(rs.getBoolean("enabled"))
                .setAccountNotExpired(rs.getBoolean("account_non_expired"))
                .setAccountNonLocked(rs.getBoolean("account_non_locked"))
                .setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
    }
}
