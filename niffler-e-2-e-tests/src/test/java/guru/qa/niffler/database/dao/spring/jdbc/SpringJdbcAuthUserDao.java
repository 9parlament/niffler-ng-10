package guru.qa.niffler.database.dao.spring.jdbc;

import guru.qa.niffler.database.dao.AuthUserDao;
import guru.qa.niffler.database.dao.spring.jdbc.mapper.AuthUserRowMapper;
import guru.qa.niffler.model.entity.AuthUserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class SpringJdbcAuthUserDao implements AuthUserDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public AuthUserEntity save(AuthUserEntity userEntity) {
        String insertSql = """
                INSERT INTO "user" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired)
                VALUES (?, ?, ?, ?, ?, ?);
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
                    PreparedStatement statement = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                    statement.setString(1, userEntity.getUsername());
                    statement.setString(2, userEntity.getPassword());
                    statement.setBoolean(3, userEntity.getEnabled());
                    statement.setBoolean(4, userEntity.getAccountNonExpired());
                    statement.setBoolean(5, userEntity.getAccountNonLocked());
                    statement.setBoolean(6, userEntity.getCredentialsNonExpired());
                    return statement;
                },
                keyHolder);
        UUID generatedKey = (UUID) keyHolder.getKeys().get("id");
        return userEntity.setId(generatedKey);
    }

    @Override
    public List<AuthUserEntity> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM \"user\"",
                AuthUserRowMapper.INSTANCE);
    }

    @Override
    public void deleteById(UUID id) {
        jdbcTemplate.update("DELETE FROM \"user\" WHERE id = ?", id);
    }
}
