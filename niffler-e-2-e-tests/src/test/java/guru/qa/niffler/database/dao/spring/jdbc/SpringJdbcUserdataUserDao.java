package guru.qa.niffler.database.dao.spring.jdbc;

import guru.qa.niffler.database.dao.UserdataUserDao;
import guru.qa.niffler.database.dao.spring.jdbc.mapper.UserdataUserRowMapper;
import guru.qa.niffler.model.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class SpringJdbcUserdataUserDao implements UserdataUserDao {
    private final DataSource dataSource;

    @Override
    public UserEntity save(UserEntity user) {
        String insertSql = """
                INSERT INTO "user" (username, currency, firstname, surname, photo, photo_small, full_name)
                VALUES (?, ?, ?, ?, ?, ?, ?);
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(con -> {
                    PreparedStatement statement = con.prepareStatement(insertSql);
                    statement.setString(1, user.getUsername());
                    statement.setString(2, user.getCurrency().name());
                    statement.setString(3, user.getFirstname());
                    statement.setString(4, user.getSurname());
                    statement.setBytes(5, user.getPhoto());
                    statement.setBytes(6, user.getPhotoSmall());
                    statement.setString(7, user.getFullname());
                    return statement;
                },
                keyHolder
        );
        UUID generatedKey = (UUID) keyHolder.getKeys().get("id");
        return user.setId(generatedKey);
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return Optional.ofNullable(jdbcTemplate.queryForObject(
                "SELECT FROM \"user\" WHERE id = ?",
                UserdataUserRowMapper.INSTANCE,
                id));
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return Optional.ofNullable(jdbcTemplate.queryForObject(
                "SELECT FROM \"user\" WHERE username = ?",
                UserdataUserRowMapper.INSTANCE,
                username));
    }

    @Override
    public List<UserEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query(
                "SELECT FROM \"user\"",
                UserdataUserRowMapper.INSTANCE
        );
    }

    @Override
    public void deleteById(UUID id) {
        String deleteSql = "DELETE FROM \"user\" WHERE id = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(deleteSql);
    }
}
