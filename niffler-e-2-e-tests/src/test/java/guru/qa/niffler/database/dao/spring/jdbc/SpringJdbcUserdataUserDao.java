package guru.qa.niffler.database.dao.spring.jdbc;

import guru.qa.niffler.database.dao.UserdataUserDao;
import guru.qa.niffler.database.dao.spring.jdbc.mapper.UserdataUserRowMapper;
import guru.qa.niffler.model.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class SpringJdbcUserdataUserDao implements UserdataUserDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public UserEntity save(UserEntity user) {
        String insertSql = """
                INSERT INTO "user" (username, currency, firstname, surname, photo, photo_small, full_name)
                VALUES (?, ?, ?, ?, ?, ?, ?);
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
                    PreparedStatement statement = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                    statement.setString(1, user.getUsername());
                    statement.setString(2, user.getCurrency().name());
                    statement.setString(3, user.getFirstname());
                    statement.setString(4, user.getSurname());
                    statement.setBytes(5, user.getPhoto());
                    statement.setBytes(6, user.getPhotoSmall());
                    statement.setString(7, user.getFullName());
                    return statement;
                },
                keyHolder
        );
        UUID generatedKey = (UUID) keyHolder.getKeys().get("id");
        return user.setId(generatedKey);
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        return jdbcTemplate.query(
                        "SELECT *FROM \"user\" WHERE id = ?",
                        UserdataUserRowMapper.INSTANCE,
                        id)
                .stream().findFirst();
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return jdbcTemplate.query(
                        "SELECT * FROM \"user\" WHERE username = ?",
                        UserdataUserRowMapper.INSTANCE,
                        username)
                .stream().findFirst();
    }

    @Override
    public List<UserEntity> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM \"user\"",
                UserdataUserRowMapper.INSTANCE
        );
    }

    @Override
    public void deleteById(UUID id) {
        jdbcTemplate.update("DELETE FROM \"user\" WHERE id = ?", id);
    }
}
