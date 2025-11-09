package guru.qa.niffler.database.repository.spring.jdbc;

import guru.qa.niffler.database.repository.AuthUserRepository;
import guru.qa.niffler.database.repository.spring.jdbc.extractor.AuthUserRowExtractor;
import guru.qa.niffler.model.entity.AuthUserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class AuthUserSpringJdbcRepository implements AuthUserRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public AuthUserEntity save(AuthUserEntity userEntity) {
        String userInsertSql = """
                INSERT INTO "user"(username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired)
                VALUES(?, ?, ?, ?, ?, ?);
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
                    PreparedStatement statement = con.prepareStatement(userInsertSql, Statement.RETURN_GENERATED_KEYS);
                    statement.setString(1, userEntity.getUsername());
                    statement.setString(2, userEntity.getPassword());
                    statement.setBoolean(3, userEntity.getEnabled());
                    statement.setBoolean(4, userEntity.getAccountNonExpired());
                    statement.setBoolean(5, userEntity.getAccountNonLocked());
                    statement.setBoolean(6, userEntity.getCredentialsNonExpired());
                    return statement;
                },
                keyHolder);
        UUID generatedId = (UUID) keyHolder.getKeys().get("id");
        userEntity.setId(generatedId);

        String authorityInsertSql = "INSERT INTO authority(user_id, authority) VALUES (?, ?);";
        jdbcTemplate.batchUpdate(authorityInsertSql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setObject(1, userEntity.getId());
                        ps.setString(2, userEntity.getAuthorities().get(i).getAuthority());
                    }

                    @Override
                    public int getBatchSize() {
                        return 2;
                    }
                });
        return userEntity;
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        String selectSql = """
                SELECT u.*,
                a.id AS authority_id,
                a.authority AS authority_authority
                FROM "user" u
                JOIN authority a ON u.id = a.user_id
                WHERE u.id = ?;
                """;
        return Optional.ofNullable(jdbcTemplate.query(selectSql, AuthUserRowExtractor.INSTANCE, id));
    }
}
